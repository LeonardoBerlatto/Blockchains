package chaincode.fabcar;

import com.google.gson.Gson;
import com.owlike.genson.Genson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.shim.ChaincodeBase;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FabCar extends ChaincodeBase {

	private static final Genson GENSON = new Genson();

	private static Log LOGGER = LogFactory.getLog(FabCar.class);

	@Override
	public Response init(final ChaincodeStub stub) {
		return ChaincodeBase.newSuccessResponse();
	}

	@Override
	public Response invoke(ChaincodeStub stub) {
		final List<String> params = stub.getParameters();

		switch (stub.getFunction()) {
			case "queryCar":
				// Call queryCar method
				return this.queryCar(stub, params);
			case "initLedger":
				// Call initLedger method
//				return this.initLedger(stub);
			case "createCar":
				return this.createCar(stub, params);
			// return ChaincodeBase.newSuccessResponse("Create car call successful!");
			case "queryAllCars":
				// Call queryAllCars method
				return this.queryAllCars(stub);
			case "changeCarOwner":
				// Call changeCarOwner method
				return null;
			default:
				return ChaincodeBase.newErrorResponse("No such function " + stub.getFunction() + " exist");
		}
	}

	private Response queryCar(final ChaincodeStub stub, final List<String> params) {
		final String key = params.get(0);
		final String carState = stub.getStringState(key);
		LOGGER.info(carState);

		if (carState.isEmpty()) {
			// log.error("Car {} does not exist", key);
			return ChaincodeBase.newErrorResponse("Car " + key + " does not exist");
		}

		return ChaincodeBase.newSuccessResponse(carState);
	}

	private Response createCar(final ChaincodeStub stub, final List<String> params) {
		final String key = params.get(0);
		String carState = stub.getStringState(key);
		LOGGER.info(key);
		LOGGER.info(carState);

		if (!carState.isEmpty()) {
			// log.error("Car {} already exists", key);
			return ChaincodeBase.newErrorResponse("Car " + key + "already exists!");
		}

		final Car car = this.mapParamsToCarObject(params);

		carState = GENSON.serialize(car);
		LOGGER.info(carState);

		stub.putStringState(key, carState);

		return ChaincodeBase.newSuccessResponse(carState);
	}

	private Response queryAllCars(final ChaincodeStub stub) {

		final String startKey = "CAR0";
		final String endKey = "CAR999";
		final List<String> cars = new ArrayList<>();

		final QueryResultsIterator<KeyValue> results = stub.getStateByRange(startKey, endKey);

		for (KeyValue result : results) {
			cars.add(result.getStringValue());
		}

		return ChaincodeBase.newSuccessResponse(cars.toString());
	}

	private Response initLedger(final ChaincodeStub stub) {
		final Gson gson = new Gson();

		final String[] carData = {
				"{ \"make\": \"Toyota\", \"model\": \"Prius\", \"color\": \"blue\", \"owner\": \"Tomoko\" }",
				"{ \"make\": \"Ford\", \"model\": \"Mustang\", \"color\": \"red\", \"owner\": \"Brad\" }",
				"{ \"make\": \"Hyundai\", \"model\": \"Tucson\", \"color\": \"green\", \"owner\": \"Jin Soo\" }",
				"{ \"make\": \"Volkswagen\", \"model\": \"Passat\", \"color\": \"yellow\", \"owner\": \"Max\" }",
				"{ \"make\": \"Tesla\", \"model\": \"S\", \"color\": \"black\", \"owner\": \"Adrian\" }",
				"{ \"make\": \"Peugeot\", \"model\": \"205\", \"color\": \"purple\", \"owner\": \"Michel\" }",
				"{ \"make\": \"Chery\", \"model\": \"S22L\", \"color\": \"white\", \"owner\": \"Aarav\" }",
				"{ \"make\": \"Fiat\", \"model\": \"Punto\", \"color\": \"violet\", \"owner\": \"Pari\" }",
				"{ \"make\": \"Tata\", \"model\": \"nano\", \"color\": \"indigo\", \"owner\": \"Valeria\" }",
				"{ \"make\": \"Holden\", \"model\": \"Barina\", \"color\": \"brown\", \"owner\": \"Shotaro\" }"
		};

		for (int index = 0; index <= carData.length; index++) {
			final String key = String.format("CAR%03d", index);

			final Car car = gson.fromJson(carData[index], Car.class);
			final String carState = gson.toJson(car);

			stub.putStringState(key, carState);
		}

		return ChaincodeBase.newSuccessResponse("Initial ledger created with success!");
	}

	private Car mapParamsToCarObject(final List<String> params) {
		final String key = params.get(0);
		final String make = params.get(1);
		final String model = params.get(2);
		final String color = params.get(3);
		final String owner = params.get(4);

		return new Car(key, make, model, color, owner);
	}

	public static void main(String[] args) {
		new FabCar().start(args);
	}
}

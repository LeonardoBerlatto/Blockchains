package bigchaindb.ipfs.example.service.ipfs;

import java.io.IOException;
import java.util.List;

public interface IPFSService {

	List<String> insert(byte[] record) throws IOException;
}
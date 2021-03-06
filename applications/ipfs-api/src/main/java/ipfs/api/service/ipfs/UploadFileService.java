package ipfs.api.service.ipfs;

import io.ipfs.api.IPFS;
import io.ipfs.api.NamedStreamable;
import ipfs.api.exception.InvalidIpfsHashException;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author eduardo.thums
 */
@Service
public class UploadFileService {

	private IPFS ipfsClient;

	public UploadFileService(IPFS ipfsClient) {
		this.ipfsClient = ipfsClient;
	}

	public String uploadFile(final byte[] file) throws IOException {
		return ipfsClient.add(new NamedStreamable.ByteArrayWrapper(file))
				.stream()
				.map(merkleNode -> merkleNode.hash.toBase58())
				.findFirst()
				.orElseThrow(InvalidIpfsHashException::new);
	}
}

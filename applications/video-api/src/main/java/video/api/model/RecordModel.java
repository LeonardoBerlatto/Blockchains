package video.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author eduardo.thums
 */
@Getter
@Setter
@NoArgsConstructor
public class RecordModel implements Serializable {

	private static final long serialVersionUID = 6552996510289916100L;

	private Long cameraId;

	private Long startDate;

	private Long endDate;

	private Long logStartDate;

	private byte[] file;

	private String contentHash;


	public RecordModel(Long cameraId, Long startDate, Long endDate, Long logStartDate, byte[] file, String contentHash) {
		this.cameraId = cameraId;
		this.startDate = startDate;
		this.endDate = endDate;
		this.logStartDate = logStartDate;
		this.file = file;
		this.contentHash = contentHash;
	}
}


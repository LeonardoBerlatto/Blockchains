import cv2
import math
import time

from constants.video_splitter_constants import \
    NUMBER_OF_MILISECONDS_IN_A_SECOND, \
    DEFAULT_FILE_NAME, \
    DEFAULT_FILE_EXTENSION, \
    DEFAULT_FILE_ENCODING

from splitter.video_splitter import VideoSplitter


class FileSplitter(VideoSplitter):

    def __init__(self, video_path, fragments_time_interval_in_seconds):
        video_capture = cv2.VideoCapture(video_path)
        super().__init__(fragments_time_interval_in_seconds, video_capture, video_capture.get(cv2.CAP_PROP_FPS))
        self.current_next_interval_second = 0
        self.video_dimensions = (int(self.video_capture.get(cv2.CAP_PROP_FRAME_WIDTH)),
                                 int(self.video_capture.get(cv2.CAP_PROP_FRAME_HEIGHT)))

    def get_output(self, output=None):
        if output:
            super().release_output(output)
            self.starting_interval_seconds = self.next_interval_seconds

        # Specify the path and name of the video file as well as the encoding, fps and resolution
        filename = str.format(
            '{} {}.{}',
            DEFAULT_FILE_NAME,
            time.strftime('%d-%m-%Y - %H.%M.%S'),
            DEFAULT_FILE_EXTENSION
        )

        self.current_video_piece_filename = filename

        return cv2.VideoWriter(
            filename,
            cv2.VideoWriter_fourcc(*DEFAULT_FILE_ENCODING),
            self.video_fps,
            self.video_dimensions
        )

    def write_frame(self, output, frame):
        current_second = math.floor(self.video_capture.get(cv2.CAP_PROP_POS_MSEC) / NUMBER_OF_MILISECONDS_IN_A_SECOND)

        if current_second == self.current_next_interval_second:
            self.increase_next_interval()
            output = self.get_output(output)

        output.write(frame)
        return output

    def increase_next_interval(self):
        self.current_next_interval_second += self.fragments_time_interval_in_seconds
        self.next_interval_seconds = self.starting_interval_seconds + self.fragments_time_interval_in_seconds

    def split(self):
        self.starting_interval_seconds = time.time()
        output = self.get_output()
        self.increase_next_interval()

        while self.video_capture.isOpened():
            frame_exists, current_frame = self.video_capture.read()

            if frame_exists:
                output = self.write_frame(output, current_frame)
            else:
                break

        self.increase_next_interval()
        super().release_output(output)
        self.video_capture.release()
        cv2.destroyAllWindows()

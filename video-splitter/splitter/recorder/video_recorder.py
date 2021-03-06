import cv2
import time
from constants.video_recorder_constants import STANDARD_DIMENSIONS, VIDEO_TYPES

from splitter.video_splitter import VideoSplitter


class VideoRecorder(VideoSplitter):

    def __init__(
            self,
            base_filename='video',
            file_extension='avi',
            fragments_time_interval_in_seconds=10,
            video_fps=30,
            number_of_records=1,
            resolution='480p'):
        super().__init__(fragments_time_interval_in_seconds, cv2.VideoCapture(0), video_fps)
        self.base_filename = base_filename
        self.file_extension = file_extension
        self.number_of_records = number_of_records
        self.resolution = resolution

    def get_output(self, output=None):
        if output:
            super().release_output(output)
            self.starting_interval_seconds = time.time()

        self.current_video_piece_filename = self.get_full_filename(time.time())

        return cv2.VideoWriter(
            self.current_video_piece_filename,
            self.get_encoding(self.file_extension),
            self.video_fps,
            self.get_dimensions(self.resolution))

    def get_full_filename(self, starting_datime):
        final_datetime = starting_datime + self.fragments_time_interval_in_seconds

        starting_datetime_text = str(time.strftime('%d-%m-%Y - %H.%M.%S', time.localtime(starting_datime)))
        final_datetime_text = str(time.strftime('%d-%m-%Y - %H.%M.%S', time.localtime(final_datetime)))

        formatted_filename = str.format(
            "{} from {} to {}.{}",
            self.base_filename,
            starting_datetime_text,
            final_datetime_text,
            self.file_extension)

        return formatted_filename

    def get_encoding(self, extension):
        return cv2.VideoWriter_fourcc(*VIDEO_TYPES[extension])

    # grab resolution dimensions and set video capture to it.
    def get_dimensions(self, resolution='480p'):
        width, height = STANDARD_DIMENSIONS["480p"]

        if resolution in STANDARD_DIMENSIONS:
            width, height = STANDARD_DIMENSIONS[resolution]

        # Change the current caputre device to the resulting resolution
        self.change_resolution(width, height)
        return width, height

    # Set resolution for the video capture
    def change_resolution(self, width, height):
        self.video_capture.set(3, width)
        self.video_capture.set(4, height)

    def record_video(self):
        output = self.get_output()
        self.starting_interval_seconds = time.time()
        self.next_interval_seconds = self.starting_interval_seconds + self.fragments_time_interval_in_seconds
        counter = 1

        while True:
            if time.time() > self.next_interval_seconds:
                if counter >= self.number_of_records:
                    break

                counter += 1
                output = self.get_output(output)
                self.next_interval_seconds += self.fragments_time_interval_in_seconds

            # Capture frame-by-frame
            ret, frame = self.video_capture.read()

            if ret:
                output.write(frame)

        super().release_output(output)
        self.video_capture.release()
        cv2.destroyAllWindows()

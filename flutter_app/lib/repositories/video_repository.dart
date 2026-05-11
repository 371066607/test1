import '../models/video_model.dart';

class VideoRepository {
  Future<List<VideoModel>> fetchVideos() async {
    await Future.delayed(const Duration(milliseconds: 500));

    return List.generate(
      10,
      (index) => VideoModel(
        id: '$index',
        title: 'Amazing Video $index',
        creator: '@creator$index',
        likes: 1000 + index,
      ),
    );
  }
}

import 'package:flutter/material.dart';

import '../../repositories/video_repository.dart';
import '../../view_models/home_view_model.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  late HomeViewModel viewModel;

  @override
  void initState() {
    super.initState();

    viewModel = HomeViewModel(VideoRepository());
    viewModel.loadVideos();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: viewModel,
      builder: (context, _) {
        if (viewModel.isLoading) {
          return const Center(child: CircularProgressIndicator());
        }

        return PageView.builder(
          scrollDirection: Axis.vertical,
          itemCount: viewModel.videos.length,
          itemBuilder: (context, index) {
            final video = viewModel.videos[index];

            return Stack(
              fit: StackFit.expand,
              children: [
                Container(
                  color: Colors.grey.shade900,
                  child: const Center(
                    child: Icon(Icons.play_circle_fill, size: 100),
                  ),
                ),
                Positioned(
                  bottom: 100,
                  left: 16,
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        video.creator,
                        style: const TextStyle(
                          fontSize: 20,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      const SizedBox(height: 8),
                      Text(video.title),
                    ],
                  ),
                ),
                Positioned(
                  right: 16,
                  bottom: 120,
                  child: Column(
                    children: [
                      const Icon(Icons.favorite, size: 32),
                      Text('${video.likes}'),
                      const SizedBox(height: 20),
                      const Icon(Icons.comment, size: 32),
                      const SizedBox(height: 20),
                      const Icon(Icons.share, size: 32),
                    ],
                  ),
                )
              ],
            );
          },
        );
      },
    );
  }
}

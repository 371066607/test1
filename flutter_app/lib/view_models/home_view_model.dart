import 'package:flutter/material.dart';

import '../models/video_model.dart';
import '../repositories/video_repository.dart';

class HomeViewModel extends ChangeNotifier {
  final VideoRepository repository;

  HomeViewModel(this.repository);

  List<VideoModel> videos = [];

  bool isLoading = false;

  Future<void> loadVideos() async {
    isLoading = true;
    notifyListeners();

    videos = await repository.fetchVideos();

    isLoading = false;
    notifyListeners();
  }
}

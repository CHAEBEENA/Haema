package com.marchlab.haema.ui.category.movie.edit

import androidx.lifecycle.ViewModel

class EditMovieViewModel(
    private val editMovieViewStateDelegate: EditMovieViewStateDelegate
): ViewModel(), EditMovieViewStateDelegate by editMovieViewStateDelegate {
}
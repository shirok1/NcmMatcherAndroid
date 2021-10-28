package tech.shirok1.ncm.matcher.android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import tech.shirok1.ncm.matcher.android.databinding.FragmentSingleInfoBinding

class SingleInfoFragment : Fragment(R.layout.fragment_single_info) {

    private var binding: FragmentSingleInfoBinding? = null

    val model: SingleInfoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSingleInfoBinding.inflate(inflater, container, false)
        model.songName.observe(viewLifecycleOwner) {
            binding?.tvSongName?.text = it ?: getString(R.string.placeholder_song_name)
        }
        model.albumName.observe(viewLifecycleOwner) {
            binding?.tvAlbumName?.text = it ?: getString(R.string.placeholder_album_name)
        }
        model.artistName.observe(viewLifecycleOwner) {
            binding?.tvArtistName?.text = it ?: getString(R.string.placeholder_artist_name)
        }
        model.image.observe(viewLifecycleOwner) {
            binding?.imageView?.apply {
                if (it == null) {
                    setImageResource(com.google.android.material.R.drawable.mtrl_ic_error) // TODO
                } else {
                    setImageBitmap(it)
                }
            }
        }
        return binding?.root
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
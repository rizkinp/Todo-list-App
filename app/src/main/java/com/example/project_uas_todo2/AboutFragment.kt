package com.example.project_uas_todo2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.MediaController
import androidx.fragment.app.Fragment
import com.example.project_uas_todo2.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {
    private lateinit var binding: FragmentAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAboutBinding.inflate(inflater, container, false)
        val rootView = binding.root

        // Inisialisasi VideoView
        val videoUri = Uri.parse("android.resource://${requireContext().packageName}/${R.raw.vid_1}")
        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(binding.videoView2)
        binding.videoView2.setMediaController(mediaController)
        binding.videoView2.setVideoURI(videoUri)
        binding.videoView2.setOnPreparedListener {
            binding.videoView2.setOnClickListener { _ ->
                if (binding.videoView2.isPlaying) {
                    binding.videoView2.pause()
                } else {
                    binding.videoView2.start()
                }
            }
        }

        // Inisialisasi Button
        val maps = rootView.findViewById<ImageView>(R.id.maps)
        maps.setOnClickListener {
            val latitude = -7.9464010
            val longitude = 112.6156505
            val uri = "geo:$latitude,$longitude"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        }

        // Inisialisasi ImageView Instagram
        val instagram = rootView.findViewById<ImageView>(R.id.instagram)
        instagram.setOnClickListener {
            val uri = Uri.parse("http://instagram.com/_u/polinema_campus")
            val intent = Intent(Intent.ACTION_VIEW, uri)

            // Tambahkan kode untuk memeriksa apakah aplikasi Instagram terpasang pada perangkat
            intent.setPackage("com.instagram.android")

            // Jika aplikasi Instagram terpasang, buka halaman profil pengguna
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            } else {
                // Jika aplikasi Instagram tidak terpasang, buka halaman Instagram melalui browser
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/polinema_campus"))
                startActivity(webIntent)
            }
        }

        // Inisialisasi ImageView Website
        val website = rootView.findViewById<ImageView>(R.id.imageView3)
        website.setOnClickListener {
            val uri = Uri.parse("https://spmb.polinema.ac.id/info/")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }



        return rootView
    }
}
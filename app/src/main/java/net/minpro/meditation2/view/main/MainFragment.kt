package net.minpro.meditation2.view.main


import android.arch.lifecycle.Observer
import android.databinding.DataBindingUtil
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.fragment_main.*
import net.minpro.meditation2.R
import net.minpro.meditation2.databinding.FragmentMainBinding
import net.minpro.meditation2.util.PlayStatus
import net.minpro.meditation2.viewmodel.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel


/**
 * A simple [Fragment] subclass.
 *
 */
class MainFragment : Fragment() {

    private val viewModel: MainViewModel by sharedViewModel()
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container,false)
        val view = binding.root
        return view
        //return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.apply {
            viewmodel = viewModel
            setLifecycleOwner(activity)
        }

        viewModel.initParameters()

        observeViewModel()

    }

    private fun observeViewModel() {
        viewModel.playStatus.observe(this, Observer { status ->
            // アプリの見た目の部分の制御
            updateUi(status!!)

            // アプリの動きの部分の制御
            when (status) {
                PlayStatus.BEFORE_START -> {

                }
                PlayStatus.ON_START -> {
                    viewModel.countDownBeforeStart()
                }
                PlayStatus.RUNNING -> {
                    viewModel.startMeditation()
                }
                PlayStatus.PAUSE -> {
                    viewModel.pauseMeditation()
                }
                PlayStatus.END -> {
                    viewModel.finishMeditation()
                }
            }
        })
        viewModel.themePicFileResId.observe(this, Observer { themePicResId ->
            loadBackgroundImage(this, themePicResId, meditation_screen)
        })
    }

    private fun updateUi(status: Int) {
        when (status) {
            PlayStatus.BEFORE_START -> {
                binding.apply {
                    btnPlay.apply {
                        visibility = View.VISIBLE
                        setBackgroundResource(R.drawable.button_pause)
                    }
                    btnFinish.visibility = View.INVISIBLE
                    txtShowMenu.visibility = View.INVISIBLE
                }
            }
            PlayStatus.ON_START -> {
                binding.apply {
                    btnPlay.visibility = View.INVISIBLE
                    btnFinish.visibility = View.INVISIBLE
                    txtShowMenu.visibility = View.VISIBLE
                }
            }
            PlayStatus.RUNNING -> {
                binding.apply {
                    btnPlay.apply {
                        visibility = View.VISIBLE
                        setBackgroundResource(R.drawable.button_pause)
                    }
                    btnFinish.visibility = View.INVISIBLE
                    txtShowMenu.visibility = View.VISIBLE
                }
            }
            PlayStatus.PAUSE -> {
                binding.apply {
                    btnPlay.apply {
                        visibility = View.VISIBLE
                        setBackgroundResource(R.drawable.button_play)
                    }
                    btnFinish.apply {
                        visibility = View.VISIBLE
                        setBackgroundResource(R.drawable.button_finish)
                    }
                    txtShowMenu.visibility = View.VISIBLE
                }
            }
            PlayStatus.END -> {

            }
        }
    }

//    private fun loadBackgroundImage(
//        mainFragment: MainFragment,
//        themePicResId: Int?,
//        meditation_screen: ConstraintLayout?
//    ) {
//        Glide.with(mainFragment).load(themePicResId).into(object : SimpleTarget<Drawable>() {
//            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
//                meditation_screen?.background = resource
//            }
//        })
//    }
private fun loadBackgroundImage(mainFragment: MainFragment, themePicResId: Int?, meditation_screen: ConstraintLayout?) {
    Glide.with(mainFragment).load(themePicResId).into(object : SimpleTarget<Drawable>() {
        override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
            meditation_screen?.background = resource
        }
    })
}
}

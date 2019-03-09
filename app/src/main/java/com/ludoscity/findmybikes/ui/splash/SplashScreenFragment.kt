package com.ludoscity.findmybikes.ui.splash

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ludoscity.findmybikes.R

class SplashScreenFragment : Fragment() {

    private lateinit var splashScreenTextTop: TextView
    private lateinit var splashScreenTextBottom: TextView

    companion object {
        fun newInstance() = SplashScreenFragment()
    }

    private lateinit var viewModel: SplashScreenViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val inflatedViewtoReturn = inflater.inflate(R.layout.splash_screen_fragment, container, false)

        splashScreenTextTop = inflatedViewtoReturn.findViewById(R.id.splashscreen_text_top)
        splashScreenTextBottom = inflatedViewtoReturn.findViewById(R.id.splashscreen_text_bottom)

        return inflatedViewtoReturn
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SplashScreenViewModel::class.java)
        // TODO: Use the ViewModel
        /*val modelFactory = InjectorUtils.provideMainActivityViewModelFactory(activity!!)
        val findMyBikesActivityViewModel = ViewModelProviders.of(this, modelFactory).get(NearbyActivityViewModel::class.java)

        findMyBikesActivityViewModel.currentBckState.observe(this, Observer {


            when(it){
                NearbyActivityViewModel.BackgroundState.STATE_NETWORK_DOWNLOAD ->{
                    splashScreenTextTop.text = getString(R.string.downloading)

                }
                NearbyActivityViewModel.BackgroundState.STATE_MAP_REFRESH ->{

                }
                else -> {

                }

            }

        })*/
    }

}
package net.uoit.distributedsystems.soundsync.app.audio;

import android.app.Fragment;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.uoit.distributedsystems.soundsync.R;
import net.uoit.distributedsystems.soundsync.app.tools.decoder.DecoderThread;
import net.uoit.distributedsystems.soundsync.app.tools.player.AudioPlayer;
import net.uoit.distributedsystems.soundsync.app.tools.player.PlayerBufferListener;
import net.uoit.distributedsystems.soundsync.transport.Protocol;

import java.io.IOException;

/**
 * Created by nicholas on 19/11/15.
 */
public class SoundFragment extends Fragment {

    private View view;


    public static SoundFragment newInstance(String role) {
        Bundle bundle = new Bundle();
        bundle.putString("role", role);
        SoundFragment frag = new SoundFragment();
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sound, container, false);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}

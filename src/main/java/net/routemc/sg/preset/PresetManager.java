package net.routemc.sg.preset;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

public class PresetManager {

    @Getter
    private List<Preset> presets = new LinkedList<>();

    public void register(Preset preset){
        presets.add(preset);
    }
}

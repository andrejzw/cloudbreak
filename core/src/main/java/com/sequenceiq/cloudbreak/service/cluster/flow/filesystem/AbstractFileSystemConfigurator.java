package com.sequenceiq.cloudbreak.service.cluster.flow.filesystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sequenceiq.cloudbreak.service.cluster.flow.BlueprintConfigurationEntry;
import com.sequenceiq.cloudbreak.service.cluster.flow.RecipeScript;
import com.sequenceiq.cloudbreak.util.FileReaderUtils;

public abstract class AbstractFileSystemConfigurator<T extends FileSystemConfiguration> implements FileSystemConfigurator<T> {

    @Override
    public List<RecipeScript> getScripts() {
        List<RecipeScript> scripts = new ArrayList<>();
        try {
            for (FileSystemScriptConfig fsScriptConfig : getScriptConfigs()) {
                String script = FileReaderUtils.readFileFromClasspath(fsScriptConfig.getScriptLocation());
                scripts.add(new RecipeScript(script, fsScriptConfig.getClusterLifecycleEvent(), fsScriptConfig.getExecutionType()));
            }
        } catch (IOException e) {
            throw new FileSystemConfigException("Filesystem configuration scripts cannot be read.", e);
        }
        return scripts;
    }

    @Override
    public List<BlueprintConfigurationEntry> getDefaultFsProperties(T fsConfig) {
        List<BlueprintConfigurationEntry> bpConfigs = new ArrayList<>();
        String defaultFs = getDefaultFsValue(fsConfig);
        bpConfigs.add(new BlueprintConfigurationEntry("core-site", "fs.defaultFS", defaultFs));
        bpConfigs.add(new BlueprintConfigurationEntry("hbase-site", "hbase.rootdir", defaultFs + "/apps/hbase/data"));
        bpConfigs.add(new BlueprintConfigurationEntry("accumulo-site", "instance.volumes", defaultFs + "/apps/accumulo/data"));
        bpConfigs.add(new BlueprintConfigurationEntry("webhcat-site", "templeton.hive.archive", defaultFs + "/hdp/apps/${hdp.version}/hive/hive.tar.gz"));
        bpConfigs.add(new BlueprintConfigurationEntry("webhcat-site", "templeton.pig.archive", defaultFs + "/hdp/apps/${hdp.version}/pig/pig.tar.gz"));
        bpConfigs.add(new BlueprintConfigurationEntry("webhcat-site", "templeton.sqoop.archive", defaultFs + "/hdp/apps/${hdp.version}/sqoop/sqoop.tar.gz"));
        bpConfigs.add(new BlueprintConfigurationEntry(
                "webhcat-site", "templeton.streaming.jar", defaultFs + "/hdp/apps/${hdp.version}/mapreduce/hadoop-streaming.jar"));
        return bpConfigs;
    }

    protected abstract List<FileSystemScriptConfig> getScriptConfigs();

}
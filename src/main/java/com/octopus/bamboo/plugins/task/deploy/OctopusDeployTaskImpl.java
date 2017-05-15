package com.octopus.bamboo.plugins.task.deploy;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.process.ExternalProcessBuilder;
import com.atlassian.bamboo.process.ProcessService;
import com.atlassian.bamboo.task.*;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.utils.process.ExternalProcess;
import com.octopus.services.DotNetExeService;
import com.octopus.services.impl.DotNetExeServiceImpl;
import com.octopus.services.impl.ResourceServiceImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * The Bamboo Task that is used to deploy artifacts to Octopus Deploy
 */
@Component
@ExportAsService({TaskType.class})
@Named("octopusDeployTask")
public class OctopusDeployTaskImpl implements TaskType {

    private static final String OCTO_CLIENT_RESOURCE = "/octopus/OctopusTools.4.15.2.portable.tar.gz";
    private static final String OCTO_CLIENT_DEST = ".octopus/OctopusTools.4.15.2/core";
    private static final int EXPECTED_RETURN_CODE = 0;
    private static final ResourceServiceImpl RESOURCE_SERVICE = new ResourceServiceImpl();
    private static final DotNetExeService DOT_NET_EXE_SERVICE = new DotNetExeServiceImpl();

    /**
     * The service we use to execute the Octopus Deploy client.
     * <p>
     * See https://bitbucket.org/atlassian/atlassian-spring-scanner for
     * details on the @ComponentImport annotation.
     */
    @ComponentImport
    private ProcessService processService;

    @SuppressWarnings("CdiInjectionPointsInspection")
    @Inject
    public OctopusDeployTaskImpl(@NotNull final ProcessService processService) {
        this.processService = processService;
    }

    @NotNull
    public TaskResult execute(@NotNull final TaskContext taskContext) throws TaskException {
        checkNotNull(taskContext, "taskContext cannot be null");
        checkState(processService != null, "processService cannot be null");

        final BuildLogger buildLogger = taskContext.getBuildLogger();

        buildLogger.addBuildLogEntry("Starting OctopusDeployTask");

        final File octoToolsDir = RESOURCE_SERVICE.extractGZToHomeDir(
                OCTO_CLIENT_RESOURCE,
                OCTO_CLIENT_DEST,
                true);

        final List<String> commands = Arrays.asList(
                DOT_NET_EXE_SERVICE.getDotnetExe(),
                octoToolsDir.toString() + File.separator + "Octo.dll");

        final ExternalProcess process = processService.createProcess(taskContext,
                new ExternalProcessBuilder()
                        .command(commands)
                        .workingDirectory(octoToolsDir));

        return TaskResultBuilder.newBuilder(taskContext)
                .checkReturnCode(process, EXPECTED_RETURN_CODE)
                .build();
    }
}

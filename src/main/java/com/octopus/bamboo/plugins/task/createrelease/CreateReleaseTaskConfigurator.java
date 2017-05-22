package com.octopus.bamboo.plugins.task.createrelease;

import com.atlassian.bamboo.collections.ActionParametersMap;
import com.atlassian.bamboo.task.AbstractTaskConfigurator;
import com.atlassian.bamboo.task.TaskDefinition;
import com.atlassian.bamboo.utils.error.ErrorCollection;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.struts.TextProvider;
import com.octopus.constants.OctoConstants;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Task configuration, where we save and validate the settings applied
 * to the plugin.
 */
@Component
@ExportAsService({com.atlassian.bamboo.task.TaskConfigurator.class})
@Named("createReleaseTaskConfigurator")
public class CreateReleaseTaskConfigurator extends AbstractTaskConfigurator {
    private static final String API_KEY_ERROR_KEY = "octopus.apiKey.error";
    private static final String SERVER_URL_ERROR_KEY = "octopus.serverUrl.error";
    private static final String PUSH_PATTERN_ERROR_KEY = "octopus.pushPattern.error";
    private static final String PROJECT_NAME_ERROR_KEY = "octopus.projectName.error";
    private static final String RELEASE_VERSION_ERROR_KEY = "octopus.releaseVersion.error";
    @ComponentImport
    private final TextProvider textProvider;

    @Inject
    public CreateReleaseTaskConfigurator(@NotNull final TextProvider textProvider) {
        this.textProvider = textProvider;
    }

    @Override
    public Map<String, String> generateTaskConfigMap(@NotNull final ActionParametersMap params,
                                                     @Nullable final TaskDefinition previousTaskDefinition) {
        checkNotNull(params);

        final Map<String, String> config = super.generateTaskConfigMap(params, previousTaskDefinition);
        config.put(OctoConstants.SERVER_URL, params.getString(OctoConstants.SERVER_URL));
        config.put(OctoConstants.API_KEY, params.getString(OctoConstants.API_KEY));
        config.put(OctoConstants.PROJECT_NAME, params.getString(OctoConstants.PROJECT_NAME));
        config.put(OctoConstants.CHANNEL_NAME, params.getString(OctoConstants.CHANNEL_NAME));
        config.put(OctoConstants.VERBOSE_LOGGING, params.getString(OctoConstants.VERBOSE_LOGGING));
        config.put(OctoConstants.RELEASE_VERSION, params.getString(OctoConstants.RELEASE_VERSION));
        return config;
    }

    @Override
    public void populateContextForEdit(@NotNull final Map<String, Object> context,
                                       @NotNull final TaskDefinition taskDefinition) {
        checkNotNull(context);
        checkNotNull(taskDefinition);

        super.populateContextForEdit(context, taskDefinition);

        context.put(OctoConstants.SERVER_URL, taskDefinition.getConfiguration().get(OctoConstants.SERVER_URL));
        context.put(OctoConstants.API_KEY, taskDefinition.getConfiguration().get(OctoConstants.API_KEY));
        context.put(OctoConstants.PROJECT_NAME, taskDefinition.getConfiguration().get(OctoConstants.PROJECT_NAME));
        context.put(OctoConstants.CHANNEL_NAME, taskDefinition.getConfiguration().get(OctoConstants.CHANNEL_NAME));
        context.put(OctoConstants.VERBOSE_LOGGING, taskDefinition.getConfiguration().get(OctoConstants.VERBOSE_LOGGING));
        context.put(OctoConstants.RELEASE_VERSION, taskDefinition.getConfiguration().get(OctoConstants.RELEASE_VERSION));
    }

    @Override
    public void validate(@NotNull final ActionParametersMap params,
                         @NotNull final ErrorCollection errorCollection) {
        checkNotNull(params);
        checkNotNull(errorCollection);

        super.validate(params, errorCollection);

        final String sayValue = params.getString(OctoConstants.SERVER_URL);
        if (StringUtils.isEmpty(sayValue)) {
            errorCollection.addError(OctoConstants.SERVER_URL, textProvider.getText(SERVER_URL_ERROR_KEY));
        }

        final String apiKey = params.getString(OctoConstants.API_KEY);
        if (StringUtils.isEmpty(apiKey)) {
            errorCollection.addError(OctoConstants.API_KEY, textProvider.getText(API_KEY_ERROR_KEY));
        }

        final String projectValue = params.getString(OctoConstants.SERVER_URL);
        if (StringUtils.isEmpty(projectValue)) {
            errorCollection.addError(OctoConstants.PROJECT_NAME, textProvider.getText(PROJECT_NAME_ERROR_KEY));
        }

        final String releaseValue = params.getString(OctoConstants.RELEASE_VERSION);
        if (StringUtils.isEmpty(releaseValue)) {
            errorCollection.addError(OctoConstants.RELEASE_VERSION, textProvider.getText(RELEASE_VERSION_ERROR_KEY));
        }
    }
}
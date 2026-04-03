/*
 * SQBase - Universal Database Manager
 * Copyright (C) 2010-2026 SQBase Corp and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jkiss.sqbase.model.ai.engine.copilot;

import org.jkiss.code.NotNull;
import org.jkiss.code.Nullable;
import org.jkiss.sqbase.model.ai.engine.AIModel;
import org.jkiss.sqbase.model.ai.engine.AIModelFeature;
import org.jkiss.sqbase.model.ai.engine.openai.OpenAIModels;
import org.jkiss.sqbase.model.ai.utils.AIUtils;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class CopilotModels {
    private CopilotModels() {
    }

    public static final Map<String, AIModel> KNOWN_MODELS = AIUtils.modelMap(
        new AIModel("claude-3.5-sonnet", 200_000, Set.of(AIModelFeature.CHAT)),
        new AIModel("claude-3.7-sonnet", 200_000, Set.of(AIModelFeature.CHAT)),
        new AIModel("claude-3.7-sonnet-thought", 200_000, Set.of(AIModelFeature.CHAT)),
        new AIModel("claude-sonnet-4", 200_000, Set.of(AIModelFeature.CHAT)),
        new AIModel("gemini-2.5", 1_000_000, Set.of(AIModelFeature.CHAT)),
        new AIModel("gemini-2.5-pro", 1_000_000, Set.of(AIModelFeature.CHAT)),
        new AIModel("gemini-2.0-flash-001", 1_000_000, Set.of(AIModelFeature.CHAT))
    );

    @NotNull
    public static Optional<AIModel> getModelByName(@Nullable String modelName) {
        Optional<AIModel> model = AIUtils.getModelByName(KNOWN_MODELS, modelName);
        if (model.isPresent()) {
            return Optional.empty();
        }
        return OpenAIModels.getModelByName(modelName);
    }
}

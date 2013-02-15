package org.motechproject.ivr.ui.support;

import java.util.List;

import javax.annotation.PostConstruct;

import org.motechproject.decisiontree.core.DecisionTreeService;
import org.motechproject.decisiontree.core.model.AudioPrompt;
import org.motechproject.decisiontree.core.model.EventTransition;
import org.motechproject.decisiontree.core.model.Node;
import org.motechproject.decisiontree.core.model.Prompt;
import org.motechproject.decisiontree.core.model.Transition;
import org.motechproject.decisiontree.core.model.Tree;
import org.motechproject.ivr.ui.IVRUISettings;
import org.motechproject.ivr.ui.content.SoundFiles;
import org.motechproject.ivr.ui.events.Events;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Constructs the Decision Tree used in the demo
 */
@Component
public class DecisionTreeBuilder {
    private final Logger logger = LoggerFactory.getLogger(DecisionTreeBuilder.class);

    private final DecisionTreeService decisionTreeService;
    private final IVRUISettings settings;

    @Autowired
    public DecisionTreeBuilder(DecisionTreeService decisionTreeService, IVRUISettings settings) {
        this.decisionTreeService = decisionTreeService;
        this.settings = settings;
    }

    @PostConstruct
    public void buildTree() {
        logger.info("Creating a new demo decision tree");

        // spinning up a new thread so that this will not block the bundle
        // loading process
        new Thread(new Runnable() {
            @Override
            public void run() {
                deleteOldTree();
                createDecisionTree();
            }

        }).start();
    }

    private void deleteOldTree() {
        List<Tree> trees = decisionTreeService.getDecisionTrees();
        for (Tree tree : trees) {
            if ("DemoTree".equals(tree.getName())) {
                decisionTreeService.deleteDecisionTree(tree.getId());
                break;
            }
        }
    }

    private void createDecisionTree() {
        Tree tree = new Tree();
        tree.setName("DemoTree");
        Transition rootTransition = new Transition();

        rootTransition.setDestinationNode(new Node()
                .setNoticePrompts(
                        new Prompt[] { new AudioPrompt().setAudioFileUrl(settings
                                .getCmsliteUrlFor(SoundFiles.CONTINUE_CALLS)) }).setTransitions(
                        new Object[][] { { "1", getContinueTransition() }, { "3", getStopTransition() } }));
        tree.setRootTransition(rootTransition);

        decisionTreeService.saveDecisionTree(tree);
    }

    private Transition getStopTransition() {
        EventTransition transition = new EventTransition();
        transition.setEventSubject(Events.PATIENT_SELECTED_STOP);
        transition.setDestinationNode(new Node().setPrompts(new AudioPrompt().setAudioFileUrl(settings
                .getCmsliteUrlFor(SoundFiles.GOODBYE))));
        transition.setName("stop");
        return transition;
    }

    private Transition getContinueTransition() {
        EventTransition transition = new EventTransition();
        transition.setEventSubject(Events.PATIENT_SELECTED_CONTINUE);
        transition.setDestinationNode(new Node()
                .setNoticePrompts(
                        new Prompt[] { new AudioPrompt().setAudioFileUrl(settings
                                .getCmsliteUrlFor(SoundFiles.CONTINUE_CALLS)) }).setTransitions(
                        new Object[][] { { "1", getContinueTransition() }, { "3", getStopTransition() } }));
        transition.setName("continue");
        return transition;
    }
}

package cz.brmlab.yodaqa.analysis.answer;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import cz.brmlab.yodaqa.model.Question.Focus;
import cz.brmlab.yodaqa.model.TyCor.LAT;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
 * Generate LAT annotations in a CandidateAnswerCAS. This is based on the
 * answer focus and the result LAT texts should be compatible with Question.LAT
 * but the process of their generation might be different in details. */

public class LATGenerator extends JCasAnnotator_ImplBase {
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
	}

	public void process(JCas jcas) throws AnalysisEngineProcessException {
		/* A Focus is also an LAT. */
		for (Focus focus : JCasUtil.select(jcas, Focus.class)) {
			addFocusLAT(jcas, focus);
		}
	}

	protected void addFocusLAT(JCas jcas, Focus focus) {
		/* Convert focus to its lemma. */
		Token ftok = (Token) focus.getBase();
		String text = ftok.getLemma().getValue();
		double spec = 0.0;

		/* Focus may be a number... */
		if (ftok.getPos().getPosValue().matches("^CD")) {
			text = "quantity";
			spec -= 2;
		}

		addLAT(jcas, focus.getBegin(), focus.getEnd(), focus, text, spec);
	}

	protected void addLAT(JCas jcas, int begin, int end, Annotation base, String text, double spec) {
		LAT lat = new LAT(jcas);
		lat.setBegin(begin);
		lat.setEnd(end);
		lat.setBase(base);
		lat.setText(text);
		lat.setSpecificity(spec);
		lat.addToIndexes();
	}
}
package com.adiguzel.shopr.explanation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import com.adiguzel.shopr.explanation.model.AbstractExplanation;
import com.adiguzel.shopr.explanation.model.DimensionArgument;
import com.adiguzel.shopr.explanation.model.Explanation;
import com.adiguzel.shopr.explanation.model.LocationContext;

public class SurfaceGenerator {
	private ExplanationLocalizer localizer;
	private TextFormatter formatter;

	public SurfaceGenerator(ExplanationLocalizer localizer,
			TextFormatter formatter) {
		this.localizer = localizer;
		this.formatter = formatter;
	}

	public Explanation transform(AbstractExplanation abstractExplanation) {
		Explanation explanation = new Explanation();
		CharSequence dimensionArguments = renderDimensionArguments(abstractExplanation);
		CharSequence contextArguments = renderContextArguments(abstractExplanation);
		
		explanation.addPositiveReason(dimensionArguments);
		if (!contextArguments.toString().isEmpty())
			explanation.addPositiveReason(contextArguments);
		
		if(!abstractExplanation.negativeArguments().isEmpty()){
			CharSequence negativeArguments = renderNegativeArguments(abstractExplanation);
			explanation.addNegativeReason(negativeArguments);
		}

		explanation.simple(formatter.concat(dimensionArguments,
				contextArguments));
		return explanation;
	}

	private CharSequence renderNegativeArguments(
			AbstractExplanation abstractExplanation) {
		String template = localizer.getNegativeArgumentTemplate();
		String text = String.format(template, textifyForNegativeArguments(abstractExplanation.negativeArguments()));
		CharSequence formatted = formatter.fromHtml(text);
		for (DimensionArgument argument : abstractExplanation.negativeArguments()) {
			formatted = formatter.renderClickable(formatted, argument
					.dimension().attribute(), com.adiguzel.shopr.explanation.util.TextUtils
					.textOf(localizer, argument.dimension().attribute()
							.getCurrentValue()));
		}
		return formatted;
	}

	private CharSequence renderDimensionArguments(
			AbstractExplanation explanation) {

		switch (explanation.category()) {
		case BY_STRONG_ARGUMENTS:
			String template = chooseRandomOne(localizer
					.getStrongArgumentTemplates());
			return render(template, explanation.primaryArguments());
		case BY_WEAK_ARGUMENTS:
			String templ = chooseRandomOne(localizer
					.getWeakArgumentTemplates());
			CharSequence primaryPart = render(templ,
					explanation.primaryArguments());
			CharSequence supportingPart = "";
			if (explanation.hasSupportingArguments())
				supportingPart = render(
						localizer.getSupportingArgumentTemplate(),
						explanation.supportingArguments());
			return formatter.concat(primaryPart, supportingPart);

		case BY_SERENDIPITOUSITY:
			return chooseRandomOne(localizer.getSerendipitousityTemplates());
		case BY_GOOD_AVERAGE:
			return localizer.getGoodAverageTemplate();
		case BY_LAST_CRITIQUE:
			return localizer.getLastCritiqueTemplate();
		default:
			return "";
		}
	}

	private CharSequence renderContextArguments(AbstractExplanation explanation) {
		if (explanation.hasContextArguments()) {
			String template = chooseRandomOne(localizer
					.getContextArgumentTemplates());
			LocationContext locContext = (LocationContext) explanation
					.contextArguments().iterator().next().context();
			String exp = String.format(template,
					locContext.distanceToUserInMeters(explanation.item()));
			return formatter.fromHtml(exp);
		} else
			return "";
	}

	private CharSequence render(String template,
			Collection<DimensionArgument> arguments) {
		String text = String.format(template, textify(arguments));
		CharSequence formatted = formatter.fromHtml(text);
		for (DimensionArgument argument : arguments) {
			formatted = formatter.renderClickable(formatted, argument
					.dimension().attribute(), com.adiguzel.shopr.explanation.util.TextUtils
					.textOf(localizer, argument.dimension().attribute()
							.getCurrentValue()));
		}
		return formatted;
	}
	
	private String textifyForNegativeArguments(Collection<DimensionArgument> arguments) {
		Iterator<DimensionArgument> iterator = arguments.iterator();

		List<String> argumentValues = new ArrayList<String>();
		while (iterator.hasNext()) {
			DimensionArgument arg = iterator.next();
			argumentValues.add(arg.dimension()
					.attribute().currentValue().descriptor().toLowerCase(Locale.ENGLISH));
		}
		return com.adiguzel.shopr.explanation.util.TextUtils.textify(localizer,
				argumentValues);
	}

	private String textify(Collection<DimensionArgument> arguments) {
		Iterator<DimensionArgument> iterator = arguments.iterator();

		List<String> argumentValues = new ArrayList<String>();
		while (iterator.hasNext()) {
			DimensionArgument arg = iterator.next();
			argumentValues.add(arg.dimension()
					.attribute().currentValue().explanatoryDescriptor().toLowerCase(Locale.ENGLISH));
		}
		return com.adiguzel.shopr.explanation.util.TextUtils.textify(localizer,
				argumentValues);
	}

	private String chooseRandomOne(String[] texts) {
		return texts[new Random().nextInt(texts.length)];
	}

}

package com.adiguzel.shopr.explanation;

import java.util.ArrayList;
import java.util.List;

import com.adiguzel.shopr.explanation.model.Context;
import com.adiguzel.shopr.explanation.model.Explanation;
import com.uwetrottmann.shopr.algorithm.AdaptiveSelection;
import com.uwetrottmann.shopr.algorithm.Query;
import com.uwetrottmann.shopr.algorithm.Utils;
import com.uwetrottmann.shopr.algorithm.model.Item;

public class Discloser {

	public List<Recommendation> explain(List<Item> recommendedItems, Query query, List<Context> contexts) {
		List<Recommendation> explainedRecommendations = new ArrayList<Recommendation>();
		for (Item item : recommendedItems) {
			explainedRecommendations.add(new Recommendation(item, explain(item,
					query, recommendedItems, contexts)));
		}
		return explainedRecommendations;
	}

	private Explanation explain(Item item, Query query,
			List<Item> recommendedItems, List<Context> contexts) {
		Explanation abstractExplanation = new ContentSelector().select(item,
				query, recommendedItems, contexts);
		return abstractExplanation;
		//return new SurfaceGenerator().generate(abstractExplanation, query);
	}

	public static void main(String[] args) {
		AdaptiveSelection as = AdaptiveSelection.get();
		as.setInitialCaseBase(Utils.getLimitedCaseBase(), true);
		/*for (Recommendation r : new Discloser().explain(as.getRecommendations(),
				as.getCurrentQuery())) {
			System.out.println("" + r.item().name());
			System.out.println("Primary");
			for (DimensionArgument arg : r.explanation().primaryArguments()) {
				System.out.println("" + arg.getType());
			}
			System.out.println("Secondary");
			for (DimensionArgument arg : r.explanation().supportingArguments()) {
				System.out.println("" + arg.getType());
			}
		}*/
	}
}

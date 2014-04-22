package com.uwetrottmann.shopr.adapters.explanation;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;

import com.uwetrottmann.shopr.R;
import com.uwetrottmann.shopr.model.SectionItem;
import com.uwetrottmann.shopr.ui.FavouriteItemListFragment;
import com.uwetrottmann.shopr.ui.explanation.ShopMapFragmentExplanation;

public class FavouritesPager extends SlidingTabStripPagerAdapter{
	
	public FavouritesPager(Fragment fragment) {
		super(fragment);
	}

	protected List<SectionItem> createFragmentSections() {
		List<SectionItem> sections = new ArrayList<SectionItem>();

		sections.add(new SectionItem()
				.title(getFragment().getString(R.string.title_favourites)).fragment(
						FavouriteItemListFragment.newInstance()));

		sections.add(new SectionItem().title(getFragment().getString(R.string.title_map))
				.fragment(ShopMapFragmentExplanation.newInstance()));

		return sections;
	}

}
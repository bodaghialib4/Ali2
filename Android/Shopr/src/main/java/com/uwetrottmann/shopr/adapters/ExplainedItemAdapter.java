package com.uwetrottmann.shopr.adapters;

import java.text.NumberFormat;
import java.util.Locale;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.adiguzel.shopr.explanation.Recommendation;
import com.adiguzel.shopr.explanation.model.AbstractExplanation;
import com.adiguzel.shopr.explanation.model.Argument.Type;
import com.adiguzel.shopr.explanation.model.ContextArgument;
import com.adiguzel.shopr.explanation.model.DimensionArgument;
import com.adiguzel.shopr.explanation.model.LocationContext;
import com.squareup.picasso.Picasso;
import com.uwetrottmann.androidutils.CheatSheet;
import com.uwetrottmann.shopr.R;
import com.uwetrottmann.shopr.algorithm.AdaptiveSelection;
import com.uwetrottmann.shopr.algorithm.model.Attributes.Attribute;
import com.uwetrottmann.shopr.algorithm.model.Color;
import com.uwetrottmann.shopr.algorithm.model.Item;
import com.uwetrottmann.shopr.listeners.ShoprListeners;
import com.uwetrottmann.shopr.listeners.ShoprListeners.OnItemCritiqueListener;
import com.uwetrottmann.shopr.listeners.ShoprListeners.OnItemFavouriteListener;
import com.uwetrottmann.shopr.listeners.ShoprListeners.OnRecommendationDisplayListener;
import com.uwetrottmann.shopr.utils.ValueConverter;

public class ExplainedItemAdapter extends ArrayAdapter<Recommendation> {

	private static final int LAYOUT = R.layout.explanation_item_layout;

	private LayoutInflater mInflater;

	private ShoprListeners.OnItemCritiqueListener mCritiqueListener;

	private OnRecommendationDisplayListener mRecommendationListener;

	private OnItemFavouriteListener mFavouriteListener;

	private Context context;

	public ExplainedItemAdapter(Context context,
			OnItemCritiqueListener critiqueListener,
			OnRecommendationDisplayListener recommendationListener,
			OnItemFavouriteListener favouriteListener) {
		super(context, LAYOUT);
		this.context = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mCritiqueListener = critiqueListener;
		mRecommendationListener = recommendationListener;
		mFavouriteListener = favouriteListener;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(LAYOUT, null);

			holder = new ViewHolder();
			holder.pictureContainer = convertView
					.findViewById(R.id.containerItemPicture);
			holder.picture = (ImageView) convertView
					.findViewById(R.id.imageViewItemPicture);
			holder.explanation = (TextView) convertView
					.findViewById(R.id.explanation);
			holder.name = (TextView) convertView
					.findViewById(R.id.textViewItemName);
			holder.label = (TextView) convertView
					.findViewById(R.id.textViewItemLabel);
			holder.price = (TextView) convertView
					.findViewById(R.id.textViewItemPrice);
			holder.buttonLike = (ImageButton) convertView
					.findViewById(R.id.imageButtonItemLike);
			holder.buttonDislike = (ImageButton) convertView
					.findViewById(R.id.imageButtonItemDislike);
			holder.buttonFavourite = (ImageButton) convertView
					.findViewById(R.id.imageButtonItemFavourite);
			holder.lastCritiqueTag = convertView
					.findViewById(R.id.textViewItemLastCritiqueLabel);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final Recommendation recommendation = getItem(position);
		final Item item = getItem(position).item();
		
		holder.explanation.setText(recommendation.explanation().simple());
		holder.explanation.setMovementMethod(LinkMovementMethod.getInstance());
		//holder.explanation.setText(Html.fromHtml(debugExplanationText(explanation)));

		holder.name.setText(item.name());
		holder.label.setText(ValueConverter.getLocalizedStringForValue(
				getContext(), item.attributes().getAttributeById(Color.ID)
						.currentValue().descriptor()));
		holder.price.setText(NumberFormat.getCurrencyInstance(Locale.GERMANY)
				.format(item.price().doubleValue()));
		holder.buttonLike.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCritiqueListener != null) {
					mCritiqueListener.onItemCritique(item, true);
				}
			}
		});
		CheatSheet.setup(holder.buttonLike, R.string.like);
		holder.buttonDislike.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCritiqueListener != null) {
					mCritiqueListener.onItemCritique(item, false);
				}
			}
		});
		CheatSheet.setup(holder.buttonDislike, R.string.dislike);

		holder.buttonFavourite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mFavouriteListener != null) {
					mFavouriteListener.onItemFavourite(item);
				}
			}
		});
		CheatSheet.setup(holder.buttonFavourite, R.string.favourite);

		holder.pictureContainer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mRecommendationListener != null) {
					mRecommendationListener.onRecommendationDisplay(recommendation);
				}
			}
		});

		// last critique tag
		int lastCritiquedId = AdaptiveSelection.get().getLastCritiquedItem() != null ? AdaptiveSelection
				.get().getLastCritiquedItem().id()
				: -1;
		holder.lastCritiqueTag
				.setVisibility(item.id() == lastCritiquedId ? View.VISIBLE
						: View.GONE);

		// load picture
		Picasso.with(getContext())
				.load(item.mainImage())
				.placeholder(null)
				.error(R.drawable.ic_action_tshirt)
				.resizeDimen(R.dimen.default_image_width,
						R.dimen.default_image_height).centerCrop()
				.into(holder.picture);

		return convertView;
	}
	
	@SuppressWarnings("unused")
	private String debugExplanationText(AbstractExplanation explanation) {
		String explanationText = "1 - ";
		for (DimensionArgument arg : explanation.primaryArguments()) {
			if (arg.type() == Type.SERENDIPITOUS) {
				explanationText += context
						.getString(R.string.explanation_template_serendipidity_1);
			} else if (arg.type() == Type.GOOD_AVERAGE) {
				explanationText += context
						.getString(R.string.explanation_template_average_item);
			} else if (arg.type() == Type.ON_DIMENSION) {
				Attribute attribute = arg.dimension().attribute();
				explanationText += String
						.format(context
								.getString(R.string.explanation_template_on_dimension_high),
								(attribute.getCurrentValue().descriptor() + "("
										+ explanation.category() 
										+ "," 
										+ arg.dimension().explanationScore()
										+ ","
										+ arg.dimension().informationScore() + ")")
										.toLowerCase(Locale.ENGLISH));
			}
		}
		explanationText += " 2- ";
		for (DimensionArgument arg : explanation.supportingArguments()) {
	
			if (arg.type() == Type.SERENDIPITOUS) {
				explanationText += context
						.getString(R.string.explanation_template_serendipidity_1);
			} else if (arg.type() == Type.GOOD_AVERAGE) {
				explanationText += context
						.getString(R.string.explanation_template_average_item);
			} else if (arg.type() == Type.ON_DIMENSION) {
				Attribute attribute = arg.dimension().attribute();
				explanationText += String
						.format(context
								.getString(R.string.explanation_template_on_dimension_high),
								(attribute.getCurrentValue().descriptor() + "("
										+ explanation.category() 
										+ "," 
										+ arg.dimension().explanationScore()
										+ ","
										+ arg.dimension().informationScore() + ")")
										.toLowerCase(Locale.ENGLISH));
			}
		}
		
		for (ContextArgument arg : explanation.contextArguments()) {
			if(arg.context() instanceof  LocationContext) {
				LocationContext context = (LocationContext) arg.context();
				explanationText += "Very close to you, only " +  context.distanceToUserInMeters(explanation.item())+ "m";
			}
				
		}
		return explanationText;
	}

	static class ViewHolder {
		View pictureContainer;
		ImageView picture;
		TextView explanation;
		TextView name;
		TextView label;
		TextView price;
		ImageButton buttonLike;
		ImageButton buttonDislike;
		ImageButton buttonFavourite;
		View lastCritiqueTag;
	}

}

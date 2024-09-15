package app.organicmaps.routing;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import app.organicmaps.Framework;
import app.organicmaps.R;
import app.organicmaps.util.DateUtils;
import app.organicmaps.util.Distance;
import app.organicmaps.util.UiUtils;

final

public class RoutePlanAdapter extends RecyclerView.Adapter<RoutePlanAdapter.RoutePlanViewHolder>
{
  Context mContext;
  RouteMarkData[] mRouteMarkData;

  public RoutePlanAdapter(Context context, RouteMarkData[] routeMarkData)
  {
    mContext = context;
    mRouteMarkData = routeMarkData;
  }

  @NonNull
  @Override
  public RoutePlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
  {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_plan_list_item,
                                                                 parent, false);

    return new RoutePlanViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull RoutePlanViewHolder holder, int position)
  {
    int markPos = mRouteMarkData.length - 1 - position;
    int iconId;

    if (mRouteMarkData[markPos].mPointType == 0)
    {
      iconId = R.drawable.route_point_start;
    }
    else if (mRouteMarkData[markPos].mPointType == 1)
    {
      // Intermediate stop.
      TypedArray iconArray = mContext.getResources().obtainTypedArray(R.array.route_stop_icons);
      iconId = iconArray.getResourceId(mRouteMarkData[markPos].mIntermediateIndex,
              R.drawable.route_point_01);
    }
    else
      iconId = R.drawable.route_point_finish;

    holder.mImageViewIcon.setImageDrawable(AppCompatResources.getDrawable(mContext, iconId));

    holder.mTextViewEta.setText(DateUtils.getEstimateTimeString(mContext,
                                mRouteMarkData[markPos].mTimeSec));

    if (mRouteMarkData[markPos].mPointType == 0)
    {
      UiUtils.hide(holder.mTextViewDot1);
      UiUtils.hide(holder.mTextViewTime);
      UiUtils.hide(holder.mTextViewDot2);
      UiUtils.hide(holder.mTextViewDistance);
    }
    else
    {
      holder.mTextViewTime.setText(DateUtils.getRemainingTimeString(mContext,
                                   mRouteMarkData[markPos].mTimeSec));

      Distance distance(mRouteMarkData[markPos].mDistanceMeters, Framework.)

      holder.mTextViewDistance.setText(DateUtils.getRemainingTimeString(mContext,
              mRouteMarkData[markPos].mTimeSec));
    }
  }

  @Override
  public int getItemCount()
  {
    return mRouteMarkData.length;
  }

  static class RoutePlanViewHolder extends RecyclerView.ViewHolder
  {
    @NonNull
    public final ImageView mImageViewIcon;

    @NonNull
    public final TextView mTextViewEta;

    @NonNull
    public final TextView mTextViewDot1;

    @NonNull
    public final TextView mTextViewTime;

    @NonNull
    public final TextView mTextViewDot2;

    @NonNull
    public final TextView mTextViewDistance;

    RoutePlanViewHolder(@NonNull View itemView)
    {
      super(itemView);
      mImageViewIcon = itemView.findViewById(R.id.icon);
      mTextViewEta = itemView.findViewById(R.id.eta);
      mTextViewDot1 = itemView.findViewById(R.id.dot1);
      mTextViewTime = itemView.findViewById(R.id.time);
      mTextViewDot2 = itemView.findViewById(R.id.dot2);
      mTextViewDistance = itemView.findViewById(R.id.distance);
    }
  }
}

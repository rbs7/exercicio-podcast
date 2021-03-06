package br.ufpe.cin.if710.podcast.ui.adapter;

import java.io.Serializable;
import java.util.List;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import br.ufpe.cin.if710.podcast.R;
import br.ufpe.cin.if710.podcast.domain.ItemFeed;
import br.ufpe.cin.if710.podcast.services.DownloadIntentService;
import br.ufpe.cin.if710.podcast.ui.EpisodeDetailActivity;

public class XmlFeedAdapter extends ArrayAdapter<ItemFeed> {

    int linkResource;

    public XmlFeedAdapter(Context context, int resource, List<ItemFeed> objects) {
        super(context, resource, objects);
        linkResource = resource;
    }

    /**
     * public abstract View getView (int position, View convertView, ViewGroup parent)
     * <p>
     * Added in API level 1
     * Get a View that displays the data at the specified position in the data set. You can either create a View manually or inflate it from an XML layout file. When the View is inflated, the parent View (GridView, ListView...) will apply default layout parameters unless you use inflate(int, android.view.ViewGroup, boolean) to specify a root view and to prevent attachment to the root.
     * <p>
     * Parameters
     * position	The position of the item within the adapter's data set of the item whose view we want.
     * convertView	The old view to reuse, if possible. Note: You should check that this view is non-null and of an appropriate type before using. If it is not possible to convert this view to display the correct data, this method can create a new view. Heterogeneous lists can specify their number of view types, so that this View is always of the right type (see getViewTypeCount() and getItemViewType(int)).
     * parent	The parent that this view will eventually be attached to
     * Returns
     * A View corresponding to the data at the specified position.
     */


	/*
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.itemlista, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.item_title);
		textView.setText(items.get(position).getTitle());
	    return rowView;
	}
	/**/

    //http://developer.android.com/training/improving-layouts/smooth-scrolling.html#ViewHolder
    static class ViewHolder {
        TextView item_title;
        TextView item_date;
        Button item_action;
        int downloadStatus;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(getContext(), linkResource, null);
            holder = new ViewHolder();
            holder.item_title = (TextView) convertView.findViewById(R.id.item_title);
            holder.item_date = (TextView) convertView.findViewById(R.id.item_date);
            holder.item_action = (Button) convertView.findViewById(R.id.item_action);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.item_title.setText(getItem(position).getTitle());
        holder.item_date.setText(getItem(position).getPubDate());
        holder.downloadStatus = getItem(position).getDownloadStatus();

        if (!getItem(position).getFileUri().equals("")) {
            getItem(position).setDownloadStatus(2);
            holder.downloadStatus = getItem(position).getDownloadStatus();
        }

        if (holder.downloadStatus == 0) {
            holder.item_action.setText(R.string.action_download);
        } else if (holder.downloadStatus == 1) {
            holder.item_action.setText(R.string.action_downloading);
        } else if (holder.downloadStatus == 2) {
            holder.item_action.setText(R.string.action_play);
        }

        holder.item_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent = new Intent(getContext(), EpisodeDetailActivity.class);
            intent.putExtra("ItemFeed", (Serializable) getItem(position));
            getContext().startActivity(intent);
            }
        });

        holder.item_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.downloadStatus == 0) { //baixar
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(getContext(), DownloadIntentService.class);
                        intent.putExtra("ItemFeed", (Serializable) getItem(position));
                        getContext().startService(intent);
                        Toast.makeText(getContext(), "Iniciando download...", Toast.LENGTH_SHORT).show();
                        holder.item_action.setText(R.string.action_downloading);
                        holder.downloadStatus = 1; //Baixando
                    } else {
                        Toast.makeText(getContext(), "Permissão negada", Toast.LENGTH_SHORT).show();
                    }
                } else if (holder.downloadStatus == 1) {
                    //TODO: player
                    Toast.makeText(getContext(), "Aguarde...", Toast.LENGTH_SHORT).show();
                } else if (holder.downloadStatus == 2) {
                    //TODO: player
                    Toast.makeText(getContext(), "Tocando...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return convertView;
    }
}
package com.pkesslas.brazzersit.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.pkesslas.brazzersit.Activity.OnlineGalleryActivity;
import com.pkesslas.brazzersit.R;
import com.pkesslas.brazzersit.adapter.MainGalleryAdapter;
import com.pkesslas.brazzersit.helper.FileHelper;

import java.util.ArrayList;


public class DisplayPictureOnlineGalleryFragment extends Fragment implements View.OnClickListener {
	private TextView title, thumbUp, thumbDown, postComment, point;
	private ListView commentList;
	private Context context;
	private ScrollView rootView;
	private ParseObject pictureObject;
	private ParseImageView imageView;
	private EditText comment;

	public DisplayPictureOnlineGalleryFragment(ParseObject pictureObject) {
		this.pictureObject = pictureObject;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = (ScrollView) inflater.inflate(R.layout.online_gallery_picture_display, container, false);

		Log.i("Displayblabla", "CONSTRUCT");

		TextView title = (TextView) rootView.findViewById(R.id.title);
		imageView = (ParseImageView) rootView.findViewById(R.id.picture);
		comment = (EditText) rootView.findViewById(R.id.post_comment);
		thumbUp = (TextView) rootView.findViewById(R.id.btn_up);
		thumbDown = (TextView) rootView.findViewById(R.id.btn_down);
		postComment = (TextView) rootView.findViewById(R.id.send_comment);
		point = (TextView) rootView.findViewById(R.id.point);

		point.setText(pictureObject.get("point").toString());
		title.setText(pictureObject.get("title").toString());

		ParseFile file = (ParseFile)pictureObject.get("image");

		imageView.setParseFile(file);
		imageView.loadInBackground();
		comment.setOnClickListener(this);
		postComment.setOnClickListener(this);
		thumbDown.setOnClickListener(this);
		thumbUp.setOnClickListener(this);


		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		return rootView;
	}

	private void buildListView() {
		ArrayList<String> objects = FileHelper.getAllFinalPicturePath();
		for (String path : objects) {
			Log.i("Selected path", path);
		}

		commentList = (ListView) rootView.findViewById(R.id.photo_list);
		final MainGalleryAdapter adapter = new MainGalleryAdapter(context, objects);
		commentList.setAdapter(adapter);

		commentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bundle args = new Bundle();
				args.putInt("position", position);
				//listener.onSwitchToNextHomeFragment(args);
			}
		});
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.send_comment) {
			if (this.comment.getText().length() >= 3 && this.comment.getText().length() <= 200) {
				ParseObject comment = new ParseObject("Comment");
				comment.put("text", this.comment.getText().toString());
				comment.put("image", pictureObject);
				comment.put("point", 0);
				comment.put("author", ParseUser.getCurrentUser());
				comment.saveInBackground();
				this.comment.setText("");
			} else {
				Toast.makeText(getActivity(), "Comment too short or too long.", Toast.LENGTH_LONG).show();
			}
		} else if (v.getId() == R.id.btn_up) {
			pictureObject.increment("point");
			point.setText(String.valueOf(Integer.parseInt(point.getText().toString()) + 1));
			pictureObject.saveInBackground();
		} else if (v.getId() == R.id.btn_down) {
			pictureObject.increment("point", -1);
			point.setText(String.valueOf(Integer.parseInt(point.getText().toString()) - 1));
			pictureObject.saveInBackground();
		}
	}
}

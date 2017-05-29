package com.example.weatherornot;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;
	private Weather weather;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
		
	}
	
	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragment
		FragmentManager fragmentManager = getFragmentManager();
		if (position == 0) {
			fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
					.commit();
		} else if (position == 1) {
			fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
					.commit();
		} else if (position == 2) {
			fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
					.commit();
		}
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_section1);
			break;
		case 2:
			mTitle = getString(R.string.title_section2);
			break;
		case 3:
			mTitle = getString(R.string.title_section3);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		 actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 每次划拉菜单栏都会重新创建这个顶部的菜单栏
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main, menu);
			// Log.d("onCreateOptionsMenu", "oooops");
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_info) {
			Toast.makeText(this, R.string.Author_Info, Toast.LENGTH_LONG).show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);// 传递点击的菜单列表标题给Fragment
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView;
			int secNum = getArguments().getInt(ARG_SECTION_NUMBER);
			if (secNum == 1) {
				rootView = inflater.inflate(R.layout.fragment_main, container, false);
				//为Spinner添加监听器
//				Spinner spinner = (Spinner) getActivity().findViewById(R.id.spinner1);//null
				Spinner spinner = (Spinner) rootView.findViewById(R.id.spinner1);
				if(spinner != null){
//					((MainActivity) getActivity()).prepareWeather((String) spinner.getSelectedItem());
					spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

						@Override
						public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
							Spinner spinnerInside = (Spinner) getActivity().findViewById(R.id.spinner1);
							((MainActivity) getActivity()).prepareWeather((String) spinnerInside.getSelectedItem());;
							Log.i("Spinner", "be listened");
						}

						@Override
						public void onNothingSelected(AdapterView<?> parent) {
							// TODO Auto-generated method stub
						}
					});
					Log.i("Con", "为spinner添加上listner");
				}else {
					Log.e("ERROR", "没有为spinner添加上listner");
				}
				
			} else if (secNum == 2) {
				rootView = inflater.inflate(R.layout.hourlyweb, container, false);
				WebView wView = (WebView) rootView.findViewById(R.id.wv);
				wView.loadUrl("http://m.weather.com.cn/mtown/hours?lat=30.5094956066&lon=114.42111611377");
				//为SwipeRefreshLayout添加监听器
				final SwipeRefreshLayout swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.demo_swiperefreshlayout);
				if(swipeLayout != null){
					swipeLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
					swipeLayout.setOnRefreshListener(new OnRefreshListener() {
						
						@Override
						public void onRefresh() {
							
							new Handler().postDelayed(new Runnable() {
			                    @Override
			                    public void run() {
			                    	((WebView)getActivity().findViewById(R.id.wv)).reload();
			                    	swipeLayout.setRefreshing(false);
			                    	Toast.makeText(getActivity(), "Refreash", Toast.LENGTH_SHORT).show();
			                    }
			                }, 500);
						}
					});
					Log.i("onCreateView", "成功为下拉组件添加监听器");
				}else {
					Log.e("onCreateView", "没有为下拉组件成功添加listener");
				}
				
			} else {//就是三的情况
				rootView = inflater.inflate(R.layout.fragment_settings, container, false);
			}
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
		}
	}

	private void prepareWeather(String city) {
		weather = new Weather(city);

		new AsyncTask<Void, Void, Integer>() {

			@Override
			protected Integer doInBackground(Void... params) {
				return weather.setJsonProject();
			}

			@Override
			protected void onPostExecute(Integer result) {
				TextView tView = (TextView) findViewById(R.id.textView1);
				if (tView != null) {
					if (result.intValue() == 1) {
						tView.setText("Successful！\n");
						tView.append("\n查询城市：" + weather.getCity());
						tView.append("\n当前温度：" + weather.getTemperatureNow());
						tView.append("\n感冒提示：" + weather.getColdInfo());
						tView.append("\nAQI：" + weather.getAQI() + "\n----------------\n\n未来天气预报");

						for (int i = 0; i < 5; i++) {
							if(weather.getSomeDaydata(i)!=null){
								tView.append("\n\n" + weather.getSomeDaydata(i).get("日期") + "的天气↓↓↓");
								tView.append("\n天气：" + weather.getSomeDaydata(i).get("天气"));
								tView.append("\n最高温：" + weather.getSomeDaydata(i).get("最高温"));
								tView.append("\n最低温：" + weather.getSomeDaydata(i).get("最低温"));
								tView.append("\n风力：" + weather.getSomeDaydata(i).get("风力"));
							}else {
								Log.e("ERROR","Hashmap"+i+"is null");
								tView.setText("载入中...");
							}
						}
					} else if (result.intValue() == -1) {
						tView.setText("Invalid city name");
					} else {
						tView.setText("好……好像断网了？");
					}
				}
				super.onPostExecute(result);
			}

		}.execute();

	}

}

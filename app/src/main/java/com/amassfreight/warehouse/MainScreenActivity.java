package com.amassfreight.warehouse;

import org.apache.http.Header;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.FunctionMenu;
import com.amassfreight.domain.IUser;
import com.amassfreight.domain.TreeFunMenu;
import com.amassfreight.utils.LogUtil;
import com.amassfreight.utils.SessionHelper;
import com.amassfreight.utils.Utils;
import com.loopj.android.http.TextHttpResponseHandler;

public class MainScreenActivity extends FragmentActivity implements
		ActionBar.TabListener {

	SectionsPagerAdapter mSectionsPagerAdapter;

	ViewPager mViewPager;
	private LayoutInflater mInflater;

	// private static int DEFAULT_MENU_IMAGE = R.drawable.launcher_icon;
	// private static Map<String, java.lang.Integer> menuImageMap;
	// static {
	// menuImageMap = new HashMap<String, java.lang.Integer>();
	// menuImageMap.put("ui.VA001Activity", R.drawable.launcher_icon);
	// menuImageMap.put("ui.DN001Activity", R.drawable.launcher_icon);
	// menuImageMap.put("ui.DN006Activity", R.drawable.launcher_icon);
	// menuImageMap.put("ui.PP004Activity", R.drawable.launcher_icon);
	// menuImageMap.put("ui.PP006Activity", R.drawable.launcher_icon);
	// menuImageMap.put("ui.PP008Activity_FCL", R.drawable.launcher_icon);
	// menuImageMap.put("ui.PP008Activity_SCL", R.drawable.launcher_icon);
	// menuImageMap.put("ui.PP010Activity_FCL", R.drawable.launcher_icon);
	// menuImageMap.put("ui.PP010Activity_SCL", R.drawable.launcher_icon);
	// menuImageMap.put("ui.PP012Activity", R.drawable.launcher_icon);
	// menuImageMap.put("ui.PP013Activity", R.drawable.launcher_icon);
	// menuImageMap.put("ui.RC001Activity", R.drawable.launcher_icon);
	// menuImageMap.put("ui.PP001Activity", R.drawable.launcher_icon);
	// menuImageMap.put("ui.OT001Activity", R.drawable.launcher_icon);
	// }

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_ACTION_BAR);
		this.setTheme(R.style.AppMainMenuTheme);
		setContentView(R.layout.activity_main_screen);
		IUser user = SessionHelper.getInstance().getUser();
		if (user != null) {
			this.setTitle(getTitle() + "-" + user.getUserName());
		}

		// this.requestWindowFeature(Window.FEATURE_ACTION_BAR);
		// getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		SharedPreferences sharedPref = getSharedPreferences(
				"com.amassfreight.warehouse", Context.MODE_PRIVATE);
		String pageId = sharedPref.getString("pageId", null);

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.menupager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

		int n = 0;
		if (pageId != null && !pageId.isEmpty()) {
			for (TreeFunMenu menu : SessionHelper.getInstance().getUser()
					.getMenus()) {
				if (menu.getGroupId().equals(pageId)) {
					break;
				}
				n++;
			}
		}
		if (n != 0
				&& n < SessionHelper.getInstance().getUser().getMenus().size()) {
			actionBar.setSelectedNavigationItem(n);
		}
		// getFragmentManager().beginTransaction().replace(android.R.id.content,
		// new MenuScreenPreference()).commit();

		Utils.createCacheDir();
		// ADD START 2014-09-10 ZXX
		Utils.createBackUp();
		// ADD END 2014-09-10 ZXX
		// final Activity thisActivity = this;
		// final GridView menuView = (GridView)
		// findViewById(R.id.gridView_main);
		// menuView.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view, int pos,
		// long id) {
		//
		// FunctionMenu data = (FunctionMenu) parent
		// .getItemAtPosition(pos);
		// Intent intent = new Intent(thisActivity, Utils
		// .GetClassByName(data.getMenuClass()));
		// // intent.putExtra("MenuTitle", data.getMenuName());
		//
		// startActivity(intent);
		// // menuView.setItemChecked(pos, true);
		// }
		//
		// });
		// menuView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
		//
		// List<TreeFunMenu> menus = SessionHelper.getInstance().getUser()
		// .getMenus();
		// // Iterator<TreeFunMenu> it = menus.iterator();
		// GridViewAdpter p = new GridViewAdpter(this, R.layout.grid_menu_item,
		// menus.get(0).getMenus().toArray());
		// menuView.setAdapter(p);
		KeepServerConnect();
	}

	@Override
	protected void onDestroy() {
		if (_keepThread != null) {
			_keepThread.interrupt();
		}
		super.onDestroy();
	}

	private Thread _keepThread;

	private void KeepServerConnect() {
		_keepThread = new Thread(new Runnable() {

			@Override
			public void run() {
				int nn = 0;
				Looper.prepare();
				// Looper.loop();
				while (true) {
					try {
						int n = 0;
						for (n = 0; n < 5 * 60; n++) {
							Thread.sleep(1000);
						}
						NetworkHelper.getInstance().postNoData(
								MainScreenActivity.this, "KeepConnect",
								new TextHttpResponseHandler() {

									@Override
									public void onFailure(int statusCode,
											Header[] headers,
											String responseBody, Throwable error) {
										// TODO Auto-generated method stub
										// super.onFailure(statusCode, headers,
										// responseBody, error);
										// _keepThread.interrupt();
									}

									@Override
									public void onSuccess(int statusCode,
											Header[] headers,
											String responseString) {
										// TODO Auto-generated method stub

									}

								});
					} catch (InterruptedException e) {
						return;
					}
				}
			}

		});
		_keepThread.start();
	}

	private long touchTime = 0;
	private long waitTime = 2000;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& keyCode == KeyEvent.KEYCODE_BACK) {
			long currentTime = System.currentTimeMillis();
			if ((currentTime - touchTime) >= waitTime) {
				Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
				touchTime = currentTime;
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// private int getMenuImage(String menuClass) {
	// Integer n = menuImageMap.get(menuClass);
	// if (n != null) {
	// return n.intValue();
	// }
	// return DEFAULT_MENU_IMAGE;
	// }
	//
	class GridViewAdpter extends ArrayAdapter {

		public GridViewAdpter(Context context, int resource, Object[] imgList) {
			super(context, resource, imgList);

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			LinearLayout layout = new LinearLayout(getContext());

			mInflater.inflate(R.layout.grid_menu_item, layout, true);
			ImageView imageView;

			TextView text;
			FunctionMenu data = (FunctionMenu) getItem(position);
			imageView = (ImageView) layout.findViewById(R.id.imageView_menu);

			text = (TextView) layout.findViewById(R.id.textView_menu);
			text.setText(data.getMenuName());
			imageView.setImageResource(Utils.getMenuImage(data.getMenuClass()));

			return layout;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_screen_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.logout_action:
			NetworkHelper.getInstance().postData(this, "Logout", null, null,
					null, false);
			SessionHelper.getInstance().setUser(null);
			// SessionHelper.getInstance().getAuthenUser().setPassword("abcd1234");
			Intent intent = new Intent(this, SplashActivity.class);
			startActivity(intent);
			this.finish();
			return true;

		case R.id.changepwd_action:
			intent = new Intent(this, ChangePasswordActivity.class);
			startActivity(intent);
			return true;

		case R.id.clearcache_action:
			new AlertDialog.Builder(new ContextThemeWrapper(this,
					android.R.style.Theme_Holo_Light))
			.setIcon(R.drawable.ic_launcher)
			.setTitle(R.string.app_name)
			.setCancelable(false)
			.setMessage(getString(R.string.msg_clear_cache))
			.setPositiveButton(getString(R.string.button_ok),
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int which) {
					Utils.deleteCacheDir();
					Utils.createCacheDir();
				}
			})
			.setNegativeButton(getString(R.string.button_no), null)
			.show();						
			return true;
		case R.id.clearpic_action:  // ADD 2014-09-10 ZXX 清除照片备份
			new AlertDialog.Builder(new ContextThemeWrapper(this,
					android.R.style.Theme_Holo_Light))
			.setIcon(R.drawable.ic_launcher)
			.setTitle(R.string.app_name)
			.setCancelable(false)
			.setMessage(getString(R.string.msg_clear_pic))
			.setPositiveButton(getString(R.string.button_ok),
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
						int which) {
					Utils.deleteBackUp();
					Utils.createBackUp();
				}
			})
			.setNegativeButton(getString(R.string.button_no), null)
			.show();			
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
		SharedPreferences sharedPref = getSharedPreferences(
				"com.amassfreight.warehouse", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("pageId", SessionHelper.getInstance().getUser()
				.getMenus().get(tab.getPosition()).getGroupId());
		editor.commit();
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction fragmentTransaction) {
		// TODO Auto-generated method stub

	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new MenuSectionFragment(SessionHelper
					.getInstance().getUser().getMenus().get(position));
			// Bundle args = new Bundle();
			// args.putInt(MenuSectionFragment.ARG_SECTION_NUMBER, position);
			// fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return SessionHelper.getInstance().getUser().getMenus().size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return SessionHelper.getInstance().getUser().getMenus()
					.get(position).getGroupTitle();
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	@SuppressLint("ValidFragment")
	public class MenuSectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		// public static final String ARG_SECTION_NUMBER = "section_number";
		private TreeFunMenu item;

		public MenuSectionFragment() {

		}

		public MenuSectionFragment(TreeFunMenu item) {
			this.item = item;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_menu, container,
					false);
			GridView menuView = (GridView) rootView
					.findViewById(R.id.gridView_menu);
			GridViewAdpter pp = new GridViewAdpter(MainScreenActivity.this, 0,
					item.getMenus().toArray());
			menuView.setAdapter(pp);
			menuView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int pos, long id) {

					FunctionMenu data = (FunctionMenu) parent
							.getItemAtPosition(pos);

					//// TODO: 2017/8/11 临时处理
					if (!"ui.DN007Activity".equals(data.getMenuClass())){
						Intent intent = new Intent(MainScreenActivity.this, Utils
								.GetClassByName(data.getMenuClass()));
						startActivity(intent);
					}
				}

			});
			return rootView;
		}
	}
}

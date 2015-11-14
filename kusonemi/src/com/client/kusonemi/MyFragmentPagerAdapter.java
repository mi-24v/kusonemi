package com.client.kusonemi;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*--*-*-*-*
     * Private instance fields
     *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    private final Context context;

    private final List<Section> sections;

    private final FragmentManager fragmentManager;

    private FragmentTransaction currentTransaction = null;

    private ArrayList<Fragment.SavedState> savedStatuses =
    		new ArrayList<Fragment.SavedState>();

    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    private Fragment currentPrimaryItem = null;

	//private int Page_count;

	/*
	MyFragmentPagerAdapter(FragmentManager fm){
		super(fm);
		Page_count = 1;
	}

	public void addPage(){
		Page_count++;
	}*/

	@Override
	public Fragment getItem(int arg0) {
		// TODO 自動生成されたメソッド・スタブ
		/*switch(arg0){
		default:
			return null;
		}*/
		return instantiateFragment(arg0);
	}

	/*
	@Override
	public int getCount() {
		// TODO 自動生成されたメソッド・スタブ
		return Page_count;
	}

	@Override
	public CharSequence getPageTitle(int position){
		switch(position){
		default:
			return "#Page" + String.valueOf(position);
		}
	}*/

	/*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
     * public constructors
     *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    /**
     *
     * @param context
     */
    public MyFragmentPagerAdapter(Context context,
    		FragmentManager fragmentManager) {
    	super(fragmentManager);
        this.context = context;
        this.sections = new ArrayList<Section>();
        this.fragmentManager = fragmentManager;
    }

    /**
     *
     * @param context
     */
    public MyFragmentPagerAdapter(Context context,
    		FragmentManager fragmentManager, List<Section> fragmentPages) {
    	super(fragmentManager);
        this.context = context;
        this.sections = fragmentPages;
        this.fragmentManager = fragmentManager;
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
     * FragmentStatePagerAdapter methods
     *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    public boolean isViewFromObject(View view, Object object) {
        return (((Fragment)object).getView() == view);
    }

    /** */
    @Override
    public int getCount() {
        return this.sections.size();
    }

    /** */
    @Override
    public CharSequence getPageTitle(int position) {
        return this.sections.get(position).getTitle();
    }

    @Override
    public int getItemPosition(Object object) {
        android.util.Log.v("SectionsPageAdapter", "getItemPosition");
        int index = this.fragments.indexOf(object);
        return index != -1 ? index : PagerAdapter.POSITION_NONE;
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (this.currentTransaction != null) {
            this.currentTransaction.commitAllowingStateLoss();
            this.currentTransaction = null;
            this.fragmentManager.executePendingTransactions();
        }
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment) object;
        if (fragment != currentPrimaryItem) {
            if (currentPrimaryItem != null) {
                currentPrimaryItem.setMenuVisibility(false);
            }

            if (fragment != null) {
                fragment.setMenuVisibility(true);
            }

            currentPrimaryItem = fragment;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        android.util.Log.v("SectionsPageAdapter", "instantiateItem:" + position);
        android.util.Log.v("SectionsPageAdapter", "this.fragments.size():" + this.fragments.size());
        if (this.currentTransaction == null) {
            this.currentTransaction = this.fragmentManager.beginTransaction();
        }

        if (this.fragments.size() > position) {
            Fragment fragment = (Fragment)this.fragments.get(position);
            if (fragment != null) {
                return fragment;
            }
        }

        Fragment fragment = this.instantiateFragment(position);
        if (this.savedStatuses.size() > position) {
            Fragment.SavedState fss = this.savedStatuses.get(position);
            if (fss != null) {
                try {
                    fragment.setInitialSavedState(fss);
                } catch (Exception ex) {
                    // Schon aktiv (kA was das heißt xD)
                    ex.printStackTrace();
                }
            }
        }

        while (this.fragments.size() <= position) {
            this.fragments.add(null);
        }

        fragment.setMenuVisibility(false);
        this.fragments.set(position, fragment);
        this.currentTransaction.add(container.getId(), fragment);

        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        android.util.Log.v("SectionsPageAdapter", "destroyItem:" + position);

        Fragment fragment = (Fragment)object;

        if (this.currentTransaction == null) {
            this.currentTransaction = this.fragmentManager.beginTransaction();
        }

        //
        /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
         * 削除対象のFragmentがsectionsに存在するかを確認する。
         * 同一であると識別するのは、
         * ・クラス名が一致する
         * ・argumentsが一致する
         * の二点を満たした場合。
         * - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
        String fullName = fragment.getClass().getName();
        Bundle arguments = fragment.getArguments();

        boolean isExist = false;
        for (Section section : this.sections) {
            // フラグメントのクラス名が一致するか
            if (section.getFragmentClassName().equals(fullName)) {
                if (section.getArguments() == null && arguments == null) {
                    // 一致
                    isExist = true;
                } else if (section.getArguments() == null){
                    // NULL で 個数が０なら一致
                    isExist = arguments.keySet().size() == 0;
                } else if (arguments == null){
                    // NULL で 個数が０なら一致
                    isExist = section.getArguments().keySet().size() == 0;
                } else {
                    boolean isKeyMatch = true;

                    // 引数のキーを確認し、一致しない項目を探します。
                    for(String key : section.getArguments().keySet()) {
                        if (!arguments.containsKey(key)) {
                            // キーが欠落
                            isKeyMatch = false;
                            break;
                        }

                        if (!section.getArguments().get(key).equals(arguments.get(key))) {
                            // 値が不一致
                            isKeyMatch = false;
                            break;
                        }
                    }

                    isExist = isKeyMatch;
                }
            }

            // 存在した為、検索を中断する。
            if (isExist) {
                break;
            }
        }


        if (isExist) {
            while (this.savedStatuses.size() <= position) {
                this.savedStatuses.add(null);
            }

            this.savedStatuses.set(position, this.fragmentManager.saveFragmentInstanceState(fragment));

            while (this.fragments.size() <= position) {
                this.fragments.add(null);
            }

            this.fragments.set(position, null);
        } else {
        }

        this.currentTransaction.remove(fragment);
    }

    @Override
    public Parcelable saveState() {
        Bundle state = null;
        if (savedStatuses.size() > 0) {
            state = new Bundle();
            Fragment.SavedState[] fss = new Fragment.SavedState[savedStatuses.size()];
            savedStatuses.toArray(fss);
            state.putParcelableArray("states", fss);
        }

        for (int i = 0; i < fragments.size(); i++) {
            Fragment f = fragments.get(i);
            if (f != null) {
                if (state == null) {
                    state = new Bundle();
                }
                String key = "f" + i;
                fragmentManager.putFragment(state, key, f);
            }
        }

        return state;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        if (state != null) {
            Bundle bundle = (Bundle) state;
            bundle.setClassLoader(loader);
            Parcelable[] fss = bundle.getParcelableArray("states");
            savedStatuses.clear();
            fragments.clear();
            if (fss != null) {
                for (int i = 0; i < fss.length; i++) {
                    savedStatuses.add((Fragment.SavedState) fss[i]);
                }
            }
            Iterable<String> keys = bundle.keySet();
            for (String key : keys) {
                if (key.startsWith("f")) {
                    int index = Integer.parseInt(key.substring(1));
                    Fragment fragment = fragmentManager.getFragment(bundle, key);
                    if (fragment != null) {
                        while (fragments.size() <= index) {
                            fragments.add(null);
                        }
                        fragment.setMenuVisibility(false);
                        fragments.set(index, fragment);
                    } else {
                        android.util.Log.w("SectionsPageAdapter", "Bad fragment at key " + key);
                    }
                }
            }
        }
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
     * public methods
     *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    /** */
    public Fragment instantiateFragment(int position) {
        if (this.sections.size() < position) {
            return null;
        }

        return Fragment.instantiate(
                this.context,
                this.sections.get(position).getFragmentClassName(),
                this.sections.get(position).getArguments());
    }

    /**
     * Sectionを追加する。
     *
     * @param resId タイトルに使う文字列リソースのID
     * @param fragmentClass フラグメントのクラス
     * @param arguments フラグメントを初期化するためのバンドル
     */
    public void addSection(int resId, Class<?> fragmentClass, Bundle arguments) {
        this.addSection(this.context.getString(resId), fragmentClass, arguments);
    }

    /**
     * Sectionを追加する。
     *
     * @param title タイトル
     * @param fragmentClass フラグメントのクラス
     * @param arguments フラグメントを初期化するためのBundle
     */
    public void addSection(String title, Class<?> fragmentClass,
    		Bundle arguments) {
        this.sections.add(new Section(title, fragmentClass, arguments));

        // 新しくタブが追加されたことを通知しておく
        this.notifyDataSetChanged();
    }

    /**
     * Sectionを削除する
     * @param section 削除するSection
     */
    public void removePage(Section section) {
        if (this.getCount() == 1) { return; }

        int position = this.sections.indexOf(section);

        this.sections.remove(section);

        while (this.savedStatuses.size() <= position) {
            this.savedStatuses.add(null);
        }

        this.savedStatuses.remove(position);

        while (this.fragments.size() <= position) {
            this.fragments.add(null);
        }

        this.fragments.remove(position);

        this.notifyDataSetChanged();
    }

    /**
     * Sectionを削除する
     * @param position 削除するインデックス
     */
    public void removePage(int position) {
        if (this.getCount() == 1) { return; }

        this.sections.remove(position);

        while (this.savedStatuses.size() <= position) {
            this.savedStatuses.add(null);
        }

        this.savedStatuses.remove(position);

        while (this.fragments.size() <= position) {
            this.fragments.add(null);
        }

        this.fragments.remove(position);

        this.notifyDataSetChanged();
    }

    /**
     * 指定したSectionを取得する
     * @param position index
     * @return
     */
    public Section getSection(int position) {
        return this.sections.get(position);
    }

    /**
     * Section情報を全て取得する。
     * @return
     */
    public ArrayList<Section> getSections() {
        int size = this.getCount();
        ArrayList<Section> result = new ArrayList<Section>(size);
        for (int index = 0; index < size; index++) {
            result.add(this.sections.get(index));
        }

        return result;
    }

    /**
     * 指定されたSectionのindexを取得する。
     * @param section indexを取得するSection
     * @return
     */
    public int getSectionPosition(Section section) {
        return this.sections.indexOf(section);
    }

}

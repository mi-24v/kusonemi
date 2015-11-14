package com.client.kusonemi;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class Section implements Parcelable {

	public static final Parcelable.Creator<Section> CREATOR = (
            new Parcelable.Creator<Section>() {
                public Section createFromParcel(Parcel in) {
                    Section result = new Section(in);
                    return result;
                }

                public Section[] newArray(int size) {
                    return new Section[size];
                }
            });

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*
     * Private instance fields
     *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-***-*-*-*-*/

    /**
     * 表示するタイトル
     */
    private String title;

    /**
     * フラグメントのクラス名
     */
    private String fragmentClassName;

    /**
     * フラグメントに設定する引数情報
     */
    private Bundle arguments;

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
     * Public Getter/Setter
     *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    /**
     * タイトルを取得します。
     * @return
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * フラグメントに設定する引数情報を取得します。
     * @return
     */
    public Bundle getArguments() {
        return this.arguments;
    }

    /**
     * フラグメントのクラス名を取得します。
     */
    public String getFragmentClassName() {
        return this.fragmentClassName;
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
     * Private constructors
     *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    private Section(Parcel in) {
        this.title = in.readString();
        this.fragmentClassName = in.readString();
        this.arguments = in.readBundle();
    }

    /*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
     * public constructors
     *-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*/

    /**
     *
     * @param title
     * @param fragmentClass
     * @param arguments
     */
    public Section(String title, Class<?> fragmentClass,
    		Bundle arguments) {
        this.title = title;
        this.fragmentClassName = fragmentClass.getName();
        this.arguments = arguments;
    }

    /*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
     * Parcelable methods
     *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*/

    /** */
    @Override
    public int describeContents() {
        return 0;
    }

    /** */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.title);
        out.writeString(this.fragmentClassName);
        out.writeBundle(this.arguments);
    }

}

package com.example.quickandroid.model

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

class KotlinModel() : Parcelable {

    var name: String = ""
    var age: Int = 0
    var dataList=ArrayList<ResultModel>()

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        age = parcel.readInt()
        dataList= parcel.readArrayList(ClassLoader.getSystemClassLoader()) as ArrayList<ResultModel>
    }


    class ResultModel() :Parcelable{
        var data1: Int = 0
        var data2: Int = 1

        constructor(parcel: Parcel) : this() {
            data1 = parcel.readInt()
            data2 = parcel.readInt()
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(data1)
            parcel.writeInt(data2)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<ResultModel> {
            override fun createFromParcel(parcel: Parcel): ResultModel {
                return ResultModel(parcel)
            }

            override fun newArray(size: Int): Array<ResultModel?> {
                return arrayOfNulls(size)
            }
        }
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(age)
        parcel.writeTypedList(dataList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<KotlinModel> {
        override fun createFromParcel(parcel: Parcel): KotlinModel {
            return KotlinModel(parcel)
        }

        override fun newArray(size: Int): Array<KotlinModel?> {
            return arrayOfNulls(size)
        }
    }

}
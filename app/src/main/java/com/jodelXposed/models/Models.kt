package com.jodelXposed.models

import com.google.gson.annotations.Expose

data class Location(var active: Boolean = false, var lat: Double = 0.0, var lng: Double = 0.0)

data class UDI(var active: Boolean = true, var udi: String = "")

data class HookValues(
        @JvmField @Expose var versionCode: Int = 1,

        @JvmField @Expose var updateMessage: String = "(no message)",

        /*
        * package com.jodelapp.jodelandroidv3.model.Storage;
        * type: method
        * arg 0: String.class (String str)
        * return: boolean.class
        * modifier: public
        * search term: features
        * */
        @JvmField @Expose var Class_Storage: String = "",
        @JvmField @Expose var Method_BetaHook_UnlockFeatures: String = "",

        /*
        * package com.jodelapp.jodelandroidv3.view.PhotoEditFragment;
        * type: field
        * class: ImageView.class
        * search term: setImageBitmap
        * */
        @JvmField @Expose var Class_PhotoEditFragment: String = "",
        @JvmField @Expose var Method_ImageHookValues_ImageView: String = "",

        /*
        * package com.jodelapp.jodelandroidv3.view.adapter.PostDetailRecyclerAdapter;
        * type: method
        * arg 0: com.jodelapp.jodelandroidv3.view.adapter.PostDetailRecyclerAdapter$PostViewHolder.class (PostViewHolder postViewHolder)
        * arg 1: int.class (int i)
        * return: void.class
        * modifier: public
        * search term:  postViewHolder.AP();
        * */
        @JvmField @Expose var Class_PostDetailRecyclerAdapter: String = "",
        @JvmField @Expose var Method_PostStuff_TrackPostsMethod: String = "",

        /*
        * package com.jodelapp.jodelandroidv3.features.mymenu.MyMenuPresenter;
        *
        * Settings_AddEntriesMethod:
        *       type: method
        *       args: null
        *       return: List<com.jodelapp.jodelandroidv3.view.MyMenuItem>.class
        *       modifier: private
        *       search term: arrayList.add(new MyMenuItem
        *
        * Settings_HandleClickEventsMethod:
        *       type: method
        *       arg 0: com.jodelapp.jodelandroidv3.view.MyMenuItem.class (MyMenuItem myMenuItem)
        *       return: void.class
        *       modifier: public
        *       search term: String str = myMenuItem.name;
        * */
        @JvmField @Expose var Class_MyMenuPresenter: String = "",
        @JvmField @Expose var Method_Settings_AddEntriesMethod: String = "",
        @JvmField @Expose var Method_Settings_HandleClickEventsMethod: String = "",

        /*
        * package com.jodelapp.jodelandroidv3.utilities.UniqueDeviceIdentifier;
        * type: method
        * args: null
        * return: String.class
        * modifier: public
        * search term: (getValue()); //method call to getValue
        * */
        @JvmField @Expose var Class_UniqueDeviceIdentifier: String = "",
        @JvmField @Expose var Method_UDI_GetUdiMethod: String = "",

        @JvmField @Expose var Class_LocationChangeListener: String = ""
)
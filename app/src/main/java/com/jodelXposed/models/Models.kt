package com.jodelXposed.models

class Location(var active: Boolean = false, var lat: Double = 0.0, var lng: Double = 0.0)

class UDI(var active: Boolean = true, var udi: String = "")

class Hookvalues {

    @JvmField var versionCode = 1

    /*
    * package com.jodelapp.jodelandroidv3.model.Storage;
    * type: method
    * arg 0: String.class (String str)
    * return: boolean.class
    * modifier: public
    * search term: features
    * */
    @JvmField var Class_Storage = "com.jodelapp.jodelandroidv3.model.Storage"
    @JvmField var BetaHook_UnlockFeatures = "bp"

    /*
    * package com.jodelapp.jodelandroidv3.view.PhotoEditFragment;
    * type: field
    * class: ImageView.class
    * search term: setImageBitmap
    * */
    @JvmField var Class_PhotoEditFragment = "com.jodelapp.jodelandroidv3.view.PhotoEditFragment"
    @JvmField var ImageHookValues_ImageView = "ayZ"

    /*
    * package com.jodelapp.jodelandroidv3.view.adapter.PostDetailRecyclerAdapter;
    * type: method
    * arg 0: com.jodelapp.jodelandroidv3.view.adapter.PostDetailRecyclerAdapter$PostViewHolder.class (PostViewHolder postViewHolder)
    * arg 1: int.class (int i)
    * return: void.class
    * modifier: public
    * search term:  postViewHolder.AP();
    * */
    @JvmField var Class_PostDetailRecyclerAdapter = "com.jodelapp.jodelandroidv3.view.adapter.PostDetailRecyclerAdapter"
    @JvmField var PostStuff_TrackPostsMethod = "a"


    /*
    * package com.jodelapp.jodelandroidv3.view.CreateTextPostFragment;
    * type: field
    * class: String.class
    * search term: arguments.getString("com.tellm.post.color");
    * */
    @JvmField var Class_CreateTextPostFragment = "com.jodelapp.jodelandroidv3.view.CreateTextPostFragment"
    @JvmField var PostStuff_ColorField = "axJ"

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
    @JvmField var Class_MyMenuPresenter = "com.jodelapp.jodelandroidv3.features.mymenu.MyMenuPresenter"
    @JvmField var Settings_AddEntriesMethod = "xh"
    @JvmField var Settings_HandleClickEventsMethod = "a"

    /*
    * package com.jodelapp.jodelandroidv3.data.gcm.MyGcmListenerService;
    * type: method
    * arg 0: String.class (String str)
    * arg 1: Bundle.class (Bundle bundle)
    * return: void.class
    * modifier: public
    * search term: AnalyticsUtil.u(ShareConstants.WEB_DIALOG_PARAM_MESSAGE, "received");
    * */
    @JvmField var Class_MyGcmListenerService = "com.jodelapp.jodelandroidv3.data.gcm.MyGcmListenerService"
    @JvmField var Theme_GCMReceiverMethod = "a"

    /*
    * package com.jodelapp.jodelandroidv3.utilities.UniqueDeviceIdentifier;
    * type: method
    * args: null
    * return: String.class
    * modifier: public
    * search term: (getValue()); //method call to getValue
    * */
    @JvmField var Class_UniqueDeviceIdentifier = "com.jodelapp.jodelandroidv3.utilities.UniqueDeviceIdentifier"
    @JvmField var UDI_GetUdiMethod = "za"

}
package com.example.fmxpagto;

import android.content.Context;

import com.artech.externalapi.ExternalApiDefinition;
import com.artech.externalapi.ExternalApiFactory;
import com.artech.framework.GenexusModule;

public class FMXMCliSitefModule implements GenexusModule {

	@Override
	public void initialize(Context context) {
		ExternalApiDefinition fmxMCliSitef = new ExternalApiDefinition(
				FmxMCliSitef.NAME,
				FmxMCliSitef.class
		);
		ExternalApiFactory.addApi(fmxMCliSitef);

//		UserControlDefinition basicUserControl = new UserControlDefinition(
//				BasicUserControl.NAME,
//				BasicUserControl.class
//		);
//		UcFactory.addControl(basicUserControl);
	}
}

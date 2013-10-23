/*
 * Copyright 2013 Hari Krishna Dulipudi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.dworks.apps.asecure.misc;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

public class Utils {
	public static final String BUNDLE_OPERATOR = "bundle_operator";
	public static final String BUNDLE_SIM_NUMBER = "bundle_sim_number";

	public static Bitmap drawViewOntoBitmap(View paramView) {
		Bitmap localBitmap = Bitmap.createBitmap(paramView.getWidth(),
				paramView.getHeight(), Bitmap.Config.RGB_565);
		paramView.draw(new Canvas(localBitmap));
		return localBitmap;
	}
}

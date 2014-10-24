/*
 * Copyright (c) Tuenti Technologies S.L. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.taobao.tae.buyingdemo.widget.buttonmenu.viewmodel.button;

/**
 * Interface created to represent a ButtonVM that supports a image that can change. This interface have to be
 * implemented by each item added to ButtonMenuVM that use a image that can change inside the button.
 *
 * @author "Pedro Vicente Gómez Sánchez" <pgomez@tuenti.com>
 */
public interface MutableResourceButtonVM extends ButtonVM {

	/**
	 * @return the resource id associated to the image.
	 */
	int getImageResourceId();

	/**
	 * Change the resource id associated to the image stored in the ButtonVM and notify the ButtonVMListener.
	 *
	 * @param imageId to store.
	 */
	public void setImageResourceId(final int imageId);

	/**
	 * @return an integer with the widget identifier that it's going to change the source image using the image
	 * resource identifier.
	 */
	public int getResIdToChangeResource();

}

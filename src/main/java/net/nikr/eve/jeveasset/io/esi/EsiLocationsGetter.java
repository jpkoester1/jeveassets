/*
 * Copyright 2009-2017 Contributors (see credits.txt)
 *
 * This file is part of jEveAssets.
 *
 * jEveAssets is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * jEveAssets is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jEveAssets; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package net.nikr.eve.jeveasset.io.esi;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.nikr.eve.jeveasset.data.api.accounts.EsiOwner;
import net.nikr.eve.jeveasset.data.settings.Settings;
import net.nikr.eve.jeveasset.gui.dialogs.update.UpdateTask;
import net.troja.eve.esi.ApiClient;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.model.CharacterAssetsNamesResponse;


public class EsiLocationsGetter extends AbstractEsiGetter {

	private final Map<Long, String> itemMap = new HashMap<Long, String>();

	public EsiLocationsGetter(UpdateTask updateTask, EsiOwner owner) {
		super(updateTask, owner, false, owner.getLocationsNextUpdate(), TaskType.LOCATIONS);
	}

	@Override
	protected void get(ApiClient apiClient) throws ApiException {
		Map<List<Long>, List<CharacterAssetsNamesResponse>> responses = updateList(splitList(getIDs(itemMap, owner), LOCATIONS_BATCH_SIZE), new ListHandler<List<Long>, List<CharacterAssetsNamesResponse>>() {
			@Override
			public List<CharacterAssetsNamesResponse> get(ApiClient apiClient, List<Long> t) throws ApiException {
				return getAssetsApiAuth(apiClient).postCharactersCharacterIdAssetsNames((int) owner.getOwnerID(), t, DATASOURCE, null, USER_AGENT, null);
			}
		});

		for (Map.Entry<List<Long>, List<CharacterAssetsNamesResponse>> entry : responses.entrySet()) {
			for (CharacterAssetsNamesResponse response : entry.getValue()) {
				final long itemID = response.getItemId();
				final String eveName = response.getName();
				if (!eveName.isEmpty()) { //Set name
					Settings.get().getEveNames().put(itemID, eveName);
				} else { //Remove name (Empty)
					Settings.get().getEveNames().remove(itemID);
				}
			}
		}
	}

	@Override
	protected void setNextUpdate(Date date) {
		owner.setLocationsNextUpdate(date);
	}

	@Override
	protected boolean inScope() {
		return owner.isLocations();
	}

	@Override
	protected boolean enabled() {
		if (owner.isCorporation()) {
			return false; //EsiScopes.CORPORATION_ASSETS.isEnabled();
		} else {
			return EsiScopes.CHARACTER_ASSETS.isEnabled();
		}
	}
	
}
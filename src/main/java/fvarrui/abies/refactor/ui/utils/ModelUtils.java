package fvarrui.abies.refactor.ui.utils;

import java.util.ArrayList;
import java.util.List;

import fvarrui.abies.refactor.services.items.AutorItem;
import fvarrui.abies.refactor.services.items.EditorialItem;
import fvarrui.abies.refactor.ui.model.AutorModel;
import fvarrui.abies.refactor.ui.model.EditorialModel;

public class ModelUtils {

	public static List<AutorItem> fromAutorModelToItemList(List<AutorModel> models) {
		List<AutorItem> items = new ArrayList<AutorItem>();
		for (AutorModel item : models) {
			items.add(item.toItem());
		}
		return items;
	}
	
	public static List<AutorModel> fromAutorItemToModelList(List<AutorItem> items) {
		List<AutorModel> models = new ArrayList<AutorModel>();
		for (AutorItem item : items) {
			models.add(AutorModel.fromItem(item));
		}
		return models;
	}
	
	public static List<EditorialItem> fromEditorialModelToItemList(List<EditorialModel> models) {
		List<EditorialItem> items = new ArrayList<>();
		for (EditorialModel item : models) {
			items.add(item.toItem());
		}
		return items;
	}
	
	public static List<EditorialModel> fromEditorialItemToModelList(List<EditorialItem> items) {
		List<EditorialModel> models = new ArrayList<EditorialModel>();
		for (EditorialItem item : items) {
			models.add(EditorialModel.fromItem(item));
		}
		return models;
	}
	
}

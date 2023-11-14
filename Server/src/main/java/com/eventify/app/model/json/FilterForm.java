package com.eventify.app.model.json;

import com.eventify.app.model.enums.Categories;

public record FilterForm (String title, String place, String dateStart, String dateEnd, Categories[] category, String typeEventPage) {

}

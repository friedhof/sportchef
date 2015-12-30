/*
 SportChef – Sports Competition Management Software
 Copyright (C) 2015 Marcus Fihlon

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/ <http://www.gnu.org/licenses/>>.
 */

"use strict";

var locales = {
    'de': 'deutsch',
    'en': 'english'
};

var translations = {
    init: function(){
        for (var locale in locales){
            this[locale] = [];
            // TODO: Get translations
        }
    },
    exists: function(locale){
        return this[locale] !== null && typeof this[locale] !== 'undefined';
    }
};

translations.init();


var untranslatedElements = document.querySelectorAll('[data-translate]');


for (var untranslatedElement in untranslatedElements){
    if (!isNaN(parseFloat(untranslatedElement)) && isFinite(untranslatedElement)){ // If it is actual an element and not length or item property.
        var el = untranslatedElements[untranslatedElement];
        var desiredLocale = el.dataset.translate;
        if (translations.exists(desiredLocale)){
            var possibleTranslation =  translations[desiredLocale][el.textContent];
            if (typeof possibleTranslation === 'string'){
                el.textContent = possibleTranslation;
            }
        }
    }
}
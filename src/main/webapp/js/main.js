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

/**
 * Locales which are supported for translations
 *
 * @type {{en: string}}
 */
var locales = {
    'en': 'english'
};


var translations = {
    init: function () {
        var translationContainer = document.createElement("div");
        translationContainer.setAttribute("id", "translation-container");
        var mainElement = document.getElementsByTagName("main")[0];
        var translationLink = this.createTranslationLink("de", "deutsch");
        translationContainer.appendChild(translationLink);

        for (var locale in locales) {
            this[locale] = [];
            var that = this;
            this.loadTranslation(locale, function (data) {
                that[locale] = JSON.parse(data);
            });
            translationLink = this.createTranslationLink(locale, locales[locale]);
            translationContainer.appendChild(translationLink);
        }

        mainElement.insertBefore(translationContainer, mainElement.childNodes[0]);

        this.addEventListeners();

        var currentLanguage = sessionStorage.getItem("language");
        if (currentLanguage !== null && typeof currentLanguage !== 'undefined'){
            this.translate(currentLanguage);
        }
    },
    createTranslationLink: function (locale, desc) {
        var translationLink = document.createElement("a");

        translationLink.appendChild(document.createTextNode(locale));
        translationLink.setAttribute("href", "#");
        translationLink.setAttribute("class", "translation-link");
        translationLink.setAttribute("title", desc);

        return translationLink;
    },
    addEventListeners: function () {
        var translator = this;
        var translationLinks = document.getElementsByClassName('translation-link');
        for (var translationLink in translationLinks) {
            if (!isNaN(parseFloat(translationLink)) && isFinite(translationLink)){
                translationLinks[translationLink].addEventListener('click', function () {
                    var locale = this.textContent;
                    if (locale !== "de"){
                        translator.translate(locale);
                        sessionStorage.setItem("language", locale);
                    } else{
                        translator.reverseTranslate();
                    }
                });
            }
        }
    },
    translate: function (locale) {
        if (typeof locale === 'undefined' || !this.exists(locale)){
            return;
        }
        var untranslatedElements = document.querySelectorAll('[data-translatable]');

        for (var untranslatedElement in untranslatedElements) {
            if (!isNaN(parseFloat(untranslatedElement)) && isFinite(untranslatedElement)){ // If it is actual an element and not length or item property.
                var el = untranslatedElements[untranslatedElement];
                if (translations.exists(locale)){
                    var possibleTranslation = translations[locale][el.textContent];
                    if (typeof possibleTranslation === 'string'){
                        el.textContent = possibleTranslation;
                        el.setAttribute('lang', locale);
                    }
                }
            }
        }
    },
    reverseTranslate: function(){
        var locale = sessionStorage.getItem("language");
        var translatedElements = document.querySelectorAll('[data-translatable]');

        for (var translatedElement in translatedElements){
            if (!isNaN(parseFloat(translatedElement)) && isFinite(translatedElement)){
                var el = translatedElements[translatedElement];
                for (var word in this[locale]){
                    if (el.textContent === this[locale][word]){
                        el.textContent = word;
                        break;
                    }
                }
            }
        }
        sessionStorage.removeItem("language");
    },
    loadTranslation: function (locale, callback) {
        var xobj = new XMLHttpRequest();
        xobj.overrideMimeType("application/json");
        xobj.open('GET', 'js/translations.' + locale + '.json', false);
        xobj.onreadystatechange = function () {
            if (xobj.readyState == 4 && xobj.status == "200"){
                callback(xobj.responseText);
            }
        };
        xobj.send(null);
    },
    exists: function (locale) {
        return this[locale] !== null && typeof this[locale] !== 'undefined';
    }
};

translations.init();
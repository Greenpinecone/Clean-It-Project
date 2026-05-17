/// <reference types="@angular/localize" />

import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { registerLocaleData } from '@angular/common';
import localeDeAt from '@angular/common/locales/de-AT';
import { App } from './app/app';

registerLocaleData(localeDeAt);

bootstrapApplication(App, appConfig).catch((err) => console.error(err));

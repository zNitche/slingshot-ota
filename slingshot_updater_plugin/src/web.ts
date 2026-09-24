import { WebPlugin } from '@capacitor/core';

import type { SlingshotUpdaterPlugin } from './definitions';

export class SlingshotWeb extends WebPlugin implements SlingshotUpdaterPlugin {
    async get_revision_number(): Promise<{ value: string }> {
        throw this.unavailable("unavailable on the web");
    }
}

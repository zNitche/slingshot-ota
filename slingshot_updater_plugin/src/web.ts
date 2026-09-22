import { WebPlugin } from '@capacitor/core';

import type { SlingshotUpdaterPlugin } from './definitions';

export class SlingshotWeb extends WebPlugin implements SlingshotUpdaterPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}

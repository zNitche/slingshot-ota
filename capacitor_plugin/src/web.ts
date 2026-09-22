import { WebPlugin } from '@capacitor/core';

import type { SlingshotPlugin } from './definitions';

export class SlingshotWeb extends WebPlugin implements SlingshotPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}

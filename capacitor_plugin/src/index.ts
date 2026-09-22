import { registerPlugin } from '@capacitor/core';

import type { SlingshotPlugin } from './definitions';

const Slingshot = registerPlugin<SlingshotPlugin>('Slingshot', {
  web: () => import('./web').then((m) => new m.SlingshotWeb()),
});

export * from './definitions';
export { Slingshot };

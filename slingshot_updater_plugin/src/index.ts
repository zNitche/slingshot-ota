import { registerPlugin } from '@capacitor/core';

import type { SlingshotUpdaterPlugin } from './definitions';

const SlingshotUpdater = registerPlugin<SlingshotUpdaterPlugin>('SlingshotUpdater', {
  web: () => import('./web').then((m) => new m.SlingshotWeb()),
});

export * from './definitions';
export { SlingshotUpdater };

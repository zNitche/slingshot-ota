export interface SlingshotPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}

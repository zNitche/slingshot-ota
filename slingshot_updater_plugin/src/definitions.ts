export interface SlingshotUpdaterPlugin {
    get_revision_number(): Promise<{ value: string }>;
}

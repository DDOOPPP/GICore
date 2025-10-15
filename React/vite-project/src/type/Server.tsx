export type Server = {
    id?: number;
    name: string;
    type: string;
    path: string;
    host: string;
    port: number;
    min: number;
    max: number;
}
export class CityTbl {
    private _ctyId: number;
    private _ctyName: string;

    get ctyId(): number {
        return this._ctyId;
    }

    set ctyId(newCtyId: number) {
        this._ctyId = newCtyId;
    }

     get ctyName(): string {
        return this._ctyName;
    }

    set ctyName(newCtyName: string) {
        this._ctyName = newCtyName;
    }
}

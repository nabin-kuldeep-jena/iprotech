export class State {
    private _steId: number; 
    private _steName: string;
    
    get steId(): number {
        return this._steId;
    }

    set steId(newSteId: number) {
        this._steId = newSteId;
    }
    get steName(): string {
        return this._steName;
    }

    set steName(newSteName: string) {
        this._steName = newSteName;
    }

   
}

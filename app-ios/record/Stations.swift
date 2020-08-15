//
//  Stations.swift
//  record
//
//  Created by user on 12.08.2020.
//  Copyright © 2020 user. All rights reserved.
//

import Foundation
import SwiftUI
import RadioRecord

struct Stations: View{
    
    @State private var holder: ServiceBinderHolder?
    @State private var proxy = StationViewProxy()
    @State private var disposable: BinderDisposable?
    
    var body: some View{
        StationsSwiftView(proxy: self.proxy)
            .onAppear(perform: onAppear)
            .onDisappear(perform: onDisappear)
    }
    
    private func onAppear() {
        if (self.holder == nil) {
            self.holder = ServiceBinderHolder()
        }
    
        self.disposable = self.holder!.binder.bindDisposable(view: self.proxy)
    }
    
    private func onDisappear() {
        disposable?.dispose()
    }
    
}

private class ServiceBinderHolder {
    let binder = ServiceLocator().getStationViewBinder()
    
    deinit {
        binder.onDestroy()
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        Stations()
    }
}

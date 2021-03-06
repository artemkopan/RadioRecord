//
//  StationsSwiftView.swift
//  record
//
//  Created by user on 12.08.2020.
//  Copyright © 2020 user. All rights reserved.
//

import SwiftUI
import RadioRecord
import AVFoundation

struct StationsSwiftView: View {
    
    @ObservedObject var proxy: StationViewProxy
    
    var body: some View{
        NavigationView {
            content
                .navigationBarTitle("Radio Record Stations")
                .navigationBarItems(
                    leading: ActivityIndicator(isAnimating: self.proxy.model?.isLoading ?? false, style: .medium)
            )
        }.alert(isPresented: $proxy.showErrorAlert, content: {
            let errorMessage = (proxy.effect as! StationViewEffect.Error).message.value as! String
            return Alert(title: Text("Some Error"), message: Text(errorMessage), dismissButton: .default(Text("Ok!")))
        })
    }
    
    
    
    private var content: some View {
        let stations = self.proxy.model?.data
    
        return Group{
            if(stations == nil){
                EmptyView()
            } else {
                List{
                    ForEach(0..<stations!.count,id: \.self){index in
                        Button(action: {
                            self.proxy.dispatchIntent(intent: StationViewIntent.SelectStation(station: stations![index]))
                        }){
                            HStack{
                                RemoteImage(url: stations![index].iconGray)
                                .frame(width: 100, height: 100)
                                .listRowInsets(EdgeInsets())
                                Text(stations![index].title)
                            }
                        }
                    }
                }
            }
        }
    }
}

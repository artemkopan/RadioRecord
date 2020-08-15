//
//  RemoteImage.swift
//  record
//
//  Created by user on 12.08.2020.
//  Copyright © 2020 user. All rights reserved.
//
import SwiftUI
import Foundation

struct RemoteImage: View {

    @ObservedObject var remoteImageUrl: RemoteImageUrl

    init(url: String) {
        remoteImageUrl = RemoteImageUrl(url)
    }

    var body: some View {
        Image(uiImage: (remoteImageUrl.data.isEmpty) ? UIImage() : UIImage(data: remoteImageUrl.data)!)
            .resizable()
            .aspectRatio(contentMode: .fit)
    }
}

//
//  StationViewProxy.swift
//  record
//
//  Created by user on 21.08.2020.
//  Copyright © 2020 user. All rights reserved.
//

import Foundation
import RadioRecord


class StationViewProxy: AbsMviView<StationViewIntent, StationViewModel, StationViewEffect>, StationView, ObservableObject {
    
    @Published var model: StationViewModel?
    @Published var effect: StationViewEffect?
    @Published var showErrorAlert: Bool = false
    
    override func render(model: StationViewModel) {
        self.model = model
    }
    
    override func acceptEffect(effect: StationViewEffect) {
        self.effect = effect
        self.showErrorAlert = ((effect as? StationViewEffect.Error) != nil)
    }
    
}
